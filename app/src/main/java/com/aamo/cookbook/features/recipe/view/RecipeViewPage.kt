package com.aamo.cookbook.features.recipe.view

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.components.RecipeViewPagerIndicators
import com.aamo.cookbook.features.recipe.view.components.RecipeViewTopBar
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.features.recipe.view.screens.RecipeSettingsScreen
import com.aamo.cookbook.features.recipe.view.screens.RecipeSummaryScreen
import com.aamo.cookbook.features.recipe.view.use_cases.copyAndSaveRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.updateBookmark
import com.aamo.cookbook.features.recipe.view.use_cases.updateRating
import com.aamo.cookbook.features.recipe.view.use_cases.updateThumbnail
import com.aamo.cookbook.service.CalculatorService
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.service.TimerService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.letIf
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeViewPage(val id: Long)

class RecipeViewViewModel(
  fetchData: () -> Flow<RecipeViewRecipeModel>,
  private val updateBookmark: suspend (Boolean, RecipeBookmark) -> Unit,
  private val updateRating: suspend (Int?, RecipeRating) -> Unit,
  private val updateThumbnail: suspend (String, Recipe) -> Unit,
  private val saveAsCopy: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
) : ViewModel() {
  class ServingsState {
    val baseline = ViewModelState(1).validation { it > 0 }
    val current = ViewModelState(1).validation { it > 0 }

    val multiplier: Double get() = current.value.toDouble() / baseline.value.toDouble()
  }

  private val _recipe = MutableStateFlow<RecipeWithChaptersStepsAndIngredients?>(null)
  val recipe = _recipe.onStart { loadData(dataFlow = fetchData) }.onEach { value ->
    if (value != null && servingsState.baseline.value != value.recipe.servings) {
      servingsState.baseline.update(value.recipe.servings)
      servingsState.current.update(value.recipe.servings)
    }
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = _recipe.value
  )

  private val _bookmark = MutableStateFlow<RecipeBookmark?>(null)
  val bookmark = _bookmark.asStateFlow()

  private val _rating = MutableStateFlow<RecipeRating?>(null)
  val rating = _rating.asStateFlow()

  val servingsState = ServingsState()

  fun updateBookmark(value: Boolean) {
    if (value && _bookmark.value != null) return
    if (!value && _bookmark.value == null) return

    val recipe = _recipe.value ?: return
    val bookmark = _bookmark.value ?: RecipeBookmark(recipeId = recipe.recipe.id)

    viewModelScope.launch {
      runCatching { updateBookmark(value, bookmark) }
    }
  }

  fun updateRating(value: Int?) {
    if (_rating.value?.ratingOutOfFive == value) return

    val recipe = _recipe.value ?: return
    val rating =
      _rating.value ?: RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = value ?: 0)

    viewModelScope.launch {
      runCatching { updateRating(value, rating) }
    }
  }

  fun updateThumbnail(value: String) {
    val recipe = _recipe.value ?: return

    if (recipe.recipe.thumbnailUri == value) return

    viewModelScope.launch {
      runCatching { updateThumbnail(value, recipe.recipe) }
    }
  }

  fun saveAsCopy() {
    val recipe = _recipe.value ?: return

    viewModelScope.launch {
      runCatching { saveAsCopy(recipe) }
    }
  }

  private fun loadData(dataFlow: () -> Flow<RecipeViewRecipeModel>) {
    viewModelScope.launch {
      runCatching {
        dataFlow().collect { value ->
          _recipe.update { old ->
            old?.copy(recipe = old.recipe.copy(thumbnailUri = value.recipe.recipe.thumbnailUri))
              ?: value.recipe
          }
          _bookmark.update { value.bookmark }
          _rating.update { value.rating }
        }
      }
    }
  }
}

fun NavGraphBuilder.recipeViewPage(
  onOpenRecipeForm: (id: Long) -> Unit, onSnackbar: (SnackbarProperties) -> Unit, onBack: () -> Unit
) {
  composable<RecipeViewPage> { navStack ->
    val (id) = navStack.toRoute<RecipeViewPage>()
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipeViewViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeViewViewModel(fetchData = {
          fetchRecipe(
            fetchRecipe = { dao.getCompleteRecipeFlow(recipeId = id) },
            fetchBookmark = { dao.getBookmarkFlow(recipeId = id) },
            fetchRating = { dao.getRatingFlow(recipeId = id) })
        }, updateBookmark = { value, bookmark ->
          updateBookmark(
            bookmark = bookmark,
            value = value,
            addBookmark = { dao.upsert(it) },
            removeBookmark = { dao.delete(it) })
        }, updateRating = { value, rating ->
          updateRating(
            rating = rating,
            value = value,
            addRating = { dao.upsert(it) },
            removeRating = { dao.delete(it) })
        }, updateThumbnail = { value, recipe ->
          updateThumbnail(recipe = recipe, value = value) { dao.upsert(it) }
        }, saveAsCopy = { original ->
          copyAndSaveRecipe(recipe = original) { copy ->
            dao.upsert(copy).also { id -> if (id > 0L) onOpenRecipeForm(id) }
          }
        })
      }
    })
    val context = LocalContext.current
    val appNotFoundSnackbarMessage = stringResource(R.string.snackbar_app_not_found)

    val recipe by viewmodel.recipe.collectAsStateWithLifecycle()
    val bookmark by viewmodel.bookmark.collectAsStateWithLifecycle()
    val rating by viewmodel.rating.collectAsStateWithLifecycle()

    LoadingScreen(loading = recipe == null) {
      RecipeViewPageContent(
        recipe = checkNotNull(recipe),
        bookmark = bookmark,
        rating = rating,
        servingsState = viewmodel.servingsState,
        onEdit = { onOpenRecipeForm(id) },
        onCopy = { viewmodel.saveAsCopy() },
        onUpdateBookmark = { viewmodel.updateBookmark(it) },
        onUpdateRating = {
          viewmodel.updateRating(value = (it as Int?).letIf(rating?.ratingOutOfFive == it) { null })
        },
        onUpdateThumbnail = {
          viewmodel.updateThumbnail(
            IOService(context = context).getFileNameWithSuffixFromUri(it) ?: String.EMPTY
          )
        },
        onOpenCalculator = {
          CalculatorService.open(context = context, onError = {
            onSnackbar(SnackbarProperties(message = appNotFoundSnackbarMessage))
          })
        },
        onOpenTimer = {
          TimerService.open(context = context, onError = {
            onSnackbar(SnackbarProperties(message = appNotFoundSnackbarMessage))
          })
        },
        onBack = onBack
      )
    }
  }
}

@Composable
fun RecipeViewPageContent(
  recipe: RecipeWithChaptersStepsAndIngredients,
  bookmark: RecipeBookmark?,
  rating: RecipeRating?,
  servingsState: RecipeViewViewModel.ServingsState,
  onEdit: () -> Unit,
  onCopy: () -> Unit,
  onUpdateBookmark: (Boolean) -> Unit,
  onUpdateRating: (Int) -> Unit,
  onUpdateThumbnail: (Uri) -> Unit,
  onOpenCalculator: () -> Unit,
  onOpenTimer: () -> Unit,
  onBack: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val pagerState = rememberPagerState(pageCount = { recipe.chapters.size + 2 }, initialPage = 1)

  Scaffold(
    topBar = {
      RecipeViewTopBar(
        title = recipe.recipe.name,
        isBookmarked = bookmark != null,
        onEdit = onEdit,
        onCopy = onCopy,
        onOpenCalculator = onOpenCalculator,
        onOpenTimer = onOpenTimer,
        onUpdateBookmark = onUpdateBookmark,
        onBack = onBack
      )
    }) { paddingValues ->
    Surface(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      Column {
        Column(modifier = Modifier.weight(1f, fill = true)) {
          HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
              .fillMaxSize()
              .weight(1f, fill = true)
          ) { pageIndex ->
            when (pageIndex) {
              0 -> RecipeSettingsScreen(
                ratingOutOfFive = rating?.ratingOutOfFive ?: 0,
                thumbnailUri = recipe.recipe.thumbnailUri,
                onRatingChange = onUpdateRating,
                onThumbnailChange = onUpdateThumbnail
              )

              1 -> RecipeSummaryScreen(
                recipe = recipe,
                servings = servingsState.current.value,
                servingsMultiplier = servingsState.multiplier,
                onServingsChange = { servingsState.current.update(it) })

              else -> TODO("Chapter page")
            }
          }
        }
        HorizontalDivider()
        // TODO: progress
        RecipeViewPagerIndicators(
          pageIndex = pagerState.currentPage, recipeProgress = emptyList(), onPageChange = {
            scope.launch { pagerState.animateScrollToPage(it) }
          })
      }
    }
  }
}

@Preview
@Composable
private fun RecipeViewPageContentPreview() {
  CookbookTheme(useDarkTheme = true) {
    Surface {
      RecipeViewPageContent(
        recipe = RecipeWithChaptersStepsAndIngredients(recipe = Recipe(), chapters = emptyList()),
        bookmark = null,
        rating = null,
        servingsState = RecipeViewViewModel.ServingsState(),
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onUpdateRating = {},
        onUpdateThumbnail = {},
        onOpenCalculator = {},
        onOpenTimer = {},
        onBack = {})
    }
  }
}