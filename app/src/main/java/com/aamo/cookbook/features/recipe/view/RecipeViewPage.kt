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
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.view.components.RecipeViewPagerIndicators
import com.aamo.cookbook.features.recipe.view.components.RecipeViewTopBar
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.features.recipe.view.models.ServingsState
import com.aamo.cookbook.features.recipe.view.screens.RecipeChapterScreen
import com.aamo.cookbook.features.recipe.view.screens.RecipeSettingsScreen
import com.aamo.cookbook.features.recipe.view.screens.RecipeSummaryScreen
import com.aamo.cookbook.features.recipe.view.use_cases.copyAndSaveRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.view.use_cases.updateBookmark
import com.aamo.cookbook.features.recipe.view.use_cases.updateRating
import com.aamo.cookbook.features.recipe.view.use_cases.updateThumbnail
import com.aamo.cookbook.service.CalculatorService
import com.aamo.cookbook.service.ICalculatorService
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.service.ITimerService
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.service.TimerService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.letIf
import com.aamo.cookbook.utility.extensions.general.onFalse
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class RecipeViewPage(val id: Long)

class RecipeViewViewModel(
  fetchData: () -> Flow<RecipeViewRecipeModel?>,
  private val updateBookmark: suspend (Boolean, RecipeBookmark) -> Unit,
  private val updateRating: suspend (Int?, RecipeRating) -> Unit,
  private val updateThumbnail: suspend (String, Recipe) -> Unit,
  private val saveAsCopy: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
) : ViewModel() {
  val recipe = fetchData().transform { emit(it?.recipe) }.runningReduce { previous, new ->
    if (new == null) null
    else previous?.copy(recipe = previous.recipe.copy(thumbnailUri = new.recipe.thumbnailUri))
  }.onEach { value ->
    if (value == null) return@onEach

    if (servingsState.baseline.value != value.recipe.servings) {
      servingsState.baseline.update(value.recipe.servings)
      servingsState.current.update(value.recipe.servings)
    }
    if (progressState.values.isEmpty()) {
      progressState.add(*value.chapters.map { c -> c.steps.map { false } }.toTypedArray())
    }
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  val bookmark = fetchData().onStart { recipe.first() }.transform { emit(it?.bookmark) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  val rating = fetchData().onStart { recipe.first() }.transform { emit(it?.rating) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  val servingsState = ServingsState()
  val progressState = ViewModelStateList<List<Boolean>>()

  fun updateBookmark(value: Boolean) {
    if (value && bookmark.value != null) return
    if (!value && bookmark.value == null) return

    viewModelScope.launch {
      val recipe = recipe.value ?: return@launch
      val bookmark = bookmark.value ?: RecipeBookmark(recipeId = recipe.recipe.id)

      runCatching { updateBookmark(value, bookmark) }
    }
  }

  fun updateRating(value: Int?) {
    if (rating.value?.ratingOutOfFive == value) return


    viewModelScope.launch {
      val recipe = recipe.value ?: return@launch
      val rating = rating.value ?: RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 0)

      runCatching { updateRating(value, rating) }
    }
  }

  fun updateThumbnail(value: String) {
    val recipe = recipe.value ?: return

    if (recipe.recipe.thumbnailUri == value) return

    viewModelScope.launch {
      runCatching { updateThumbnail(value, recipe.recipe) }
    }
  }

  fun saveAsCopy() {
    val recipe = recipe.value ?: return

    viewModelScope.launch {
      runCatching { saveAsCopy(recipe) }
    }
  }

  fun openTimer(timerService: ITimerService): Boolean {
    return runCatching { timerService.open() }.isSuccess
  }

  fun startTimer(timerService: ITimerService, title: String, duration: Duration): Boolean {
    return runCatching { timerService.start(title = title, duration = duration) }.isSuccess
  }

  fun openCalculator(calculatorService: ICalculatorService): Boolean {
    return runCatching { calculatorService.open() }.isSuccess
  }
}

fun NavGraphBuilder.recipeViewPage(
  onOpenRecipeForm: (id: Long) -> Unit, onSnackbar: (SnackbarProperties) -> Unit, onBack: () -> Unit
) {
  composable<RecipeViewPage> { navStack ->
    val nameSuffix = stringResource(R.string.suffix_copy)
    val appNotFoundMessage = stringResource(R.string.snackbar_app_not_found)

    val (id) = navStack.toRoute<RecipeViewPage>()
    val context = LocalContext.current
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipeViewViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeViewViewModel(
          fetchData = { fetchRecipe(dao = dao, recipeId = id) },
          updateBookmark = { value, bookmark ->
            updateBookmark(dao = dao, bookmark = bookmark, value = value)
          },
          updateRating = { value, rating ->
            updateRating(dao = dao, rating = rating, value = value)
          },
          updateThumbnail = { value, recipe ->
            updateThumbnail(
              dao = dao,
              photoService = PhotoService(context = context),
              recipe = recipe,
              value = value
            )
          },
          saveAsCopy = { recipe ->
            copyAndSaveRecipe(
              dao = dao, recipe = recipe, nameSuffix = nameSuffix
            ).also { id ->
              if (id > 0L) onOpenRecipeForm(id)
            }
          })
      }
    })
    val recipe by viewmodel.recipe.collectAsStateWithLifecycle()
    val bookmark by viewmodel.bookmark.collectAsStateWithLifecycle()
    val rating by viewmodel.rating.collectAsStateWithLifecycle()

    LoadingScreen(loading = recipe == null) {
      RecipeViewPageContent(
        recipe = checkNotNull(recipe),
        bookmark = bookmark,
        rating = rating,
        servingsState = viewmodel.servingsState,
        progressState = viewmodel.progressState,
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
          viewmodel.openCalculator(calculatorService = CalculatorService(context = context))
            .onFalse {
              onSnackbar(SnackbarProperties(message = appNotFoundMessage))
            }
        },
        onOpenTimer = {
          viewmodel.openTimer(timerService = TimerService(context = context)).onFalse {
            onSnackbar(SnackbarProperties(message = appNotFoundMessage))
          }
        },
        onStartTimer = { title, duration ->
          viewmodel.startTimer(
            timerService = TimerService(context = context), title = title, duration = duration
          ).onFalse {
            onSnackbar(SnackbarProperties(message = appNotFoundMessage))
          }
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
  servingsState: ServingsState,
  progressState: ViewModelStateList<List<Boolean>>,
  onEdit: () -> Unit,
  onCopy: () -> Unit,
  onUpdateBookmark: (Boolean) -> Unit,
  onUpdateRating: (Int) -> Unit,
  onUpdateThumbnail: (Uri) -> Unit,
  onOpenCalculator: () -> Unit,
  onOpenTimer: () -> Unit,
  onStartTimer: (title: String, duration: Duration) -> Unit,
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

              else -> {
                val chapterIndex = pageIndex - 2

                if (chapterIndex in 0..<recipe.chapters.size) {
                  RecipeChapterScreen(
                    chapter = recipe.chapters.elementAt(chapterIndex),
                    servingsMultiplier = servingsState.multiplier,
                    isCurrentChapter = progressState.values.take(chapterIndex)
                      .all { progress -> progress.all { it } },
                    progress = progressState.values.elementAt(chapterIndex),
                    onProgressChange = { progressState.replaceAt(index = chapterIndex, item = it) },
                    onStartTimer = onStartTimer
                  )
                }
              }
            }
          }
        }
        HorizontalDivider()
        RecipeViewPagerIndicators(
          pageIndex = pagerState.currentPage,
          recipeProgress = progressState.values.map { list -> list.all { it } },
          onPageChange = {
            scope.launch { pagerState.animateScrollToPage(it) }
          })
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun RecipeViewPageContentPreview() {
  CookbookTheme {
    Surface {
      RecipeViewPageContent(
        recipe = RecipeWithChaptersStepsAndIngredients(
          recipe = Recipe(), chapters = listOf(
            ChapterWithStepsAndIngredients(
              chapter = Chapter(id = 1, name = "Chapter 1"), steps = listOf(
                StepWithIngredients(
                  step = Step(), ingredients = listOf(
                    Ingredient(id = 1, name = "Ingredient 1", amount = 1.0, unit = "dl"),
                    Ingredient(id = 2, name = "Ingredient 2", amount = 2.0, unit = "kpl"),
                    Ingredient(id = 3, name = "Ingredient 3", amount = 3.0, unit = "ml"),
                  )
                )
              )
            ),
            ChapterWithStepsAndIngredients(
              chapter = Chapter(id = 2, name = "Chapter 2"), steps = listOf(
                StepWithIngredients(
                  step = Step(), ingredients = listOf(
                    Ingredient(id = 1, name = "Ingredient 1", amount = 1.0, unit = "dl"),
                    Ingredient(id = 2, name = "Ingredient 2", amount = 2.0, unit = "kpl"),
                    Ingredient(id = 3, name = "Ingredient 3", amount = 3.0, unit = "ml"),
                  )
                )
              )
            ),
          )
        ),
        bookmark = null,
        rating = null,
        servingsState = ServingsState(),
        progressState = ViewModelStateList(listOf(listOf(true), listOf(false), listOf(false))),
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onUpdateRating = {},
        onUpdateThumbnail = {},
        onOpenCalculator = {},
        onOpenTimer = {},
        onStartTimer = { _, _ -> },
        onBack = {})
    }
  }
}