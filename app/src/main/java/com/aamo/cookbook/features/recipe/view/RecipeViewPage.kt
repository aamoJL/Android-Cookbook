package com.aamo.cookbook.features.recipe.view

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
import com.aamo.cookbook.features.recipe.view.use_cases.copyAndSaveRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.updateBookmark
import com.aamo.cookbook.service.CalculatorService
import com.aamo.cookbook.service.TimerService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeViewPage(val id: Long)

class RecipeViewViewModel(
  fetchData: () -> Flow<RecipeViewRecipeModel>,
  private val updateBookmark: suspend (Boolean, RecipeBookmark) -> Unit,
  private val saveAsCopy: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
) : ViewModel() {
  var recipe = fetchData().stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  fun updateBookmark(value: Boolean) {
    if (value && recipe.value?.bookmark != null) return
    if (!value && recipe.value?.bookmark == null) return

    viewModelScope.launch {
      runCatching {
        val bookmark =
          if (value) RecipeBookmark(recipeId = checkNotNull(recipe.value).recipe.recipe.id)
          else checkNotNull(recipe.value?.bookmark)

        updateBookmark(value, bookmark)
      }
    }
  }

  fun saveAsCopy() {
    viewModelScope.launch {
      runCatching { saveAsCopy(checkNotNull(recipe.value?.recipe)) }
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
            fetchRecipe = {
            dao.getCompleteRecipe(recipeId = id) ?: throw Error("Failed to fetch recipe")
          },
            fetchBookmark = { dao.getBookmarkFlow(recipeId = id) },
            fetchRating = { dao.getRatingFlow(recipeId = id) })
        }, updateBookmark = { value, bookmark ->
          updateBookmark(
            bookmark = bookmark,
            value = value,
            addBookmark = { dao.upsert(it) },
            removeBookmark = { dao.delete(it) })
        }, saveAsCopy = { original ->
          copyAndSaveRecipe(
            recipe = original, saveCopy = { copy ->
              dao.upsert(copy).also { id -> if (id > 0L) onOpenRecipeForm(id) }
            })
        })
      }
    })
    val context = LocalContext.current
    val appNotFoundSnackbarMessage = stringResource(R.string.snackbar_app_not_found)
    val recipe = viewmodel.recipe.collectAsStateWithLifecycle().value

    LoadingScreen(loading = recipe == null) {
      RecipeViewPageContent(
        recipe = checkNotNull(recipe).recipe,
        bookmark = recipe.bookmark,
        rating = recipe.rating,
        onEdit = { onOpenRecipeForm(id) },
        onCopy = { viewmodel.saveAsCopy() },
        onUpdateBookmark = { viewmodel.updateBookmark(it) },
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
  onEdit: () -> Unit,
  onCopy: () -> Unit,
  onUpdateBookmark: (Boolean) -> Unit,
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
            // TODO()
          }
        }
        HorizontalDivider()
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
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onOpenCalculator = {},
        onOpenTimer = {},
        onBack = {})
    }
  }
}