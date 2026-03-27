package com.aamo.cookbook.features.recipe.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.models.states.FormChapterState
import com.aamo.cookbook.features.recipe.form.models.states.FormRecipeState
import com.aamo.cookbook.features.recipe.form.screens.RecipeChapterScreen
import com.aamo.cookbook.features.recipe.form.screens.RecipeInfoScreen
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fetchCategorySuggestions
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.ifElse
import com.aamo.cookbook.utility.extensions.general.onTrue
import com.aamo.cookbook.utility.viewmodels.SavingState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormPage(val id: Long)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val saveData: suspend (fields: RecipeFormInfoFields, id: Long, thumbnail: String) -> Unit,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
  private val fetchCategorySuggestions: suspend () -> Map<String, List<String>>,
) : ViewModel() {
  val recipe = flow { emit(fetchData()) }.onEach { value ->
    isNew = value.recipe.id == 0L

    formRecipeState.fields.apply {
      name.update(value.recipe.name)
      category.update(value.recipe.category)
      subCategory.update(value.recipe.subCategory)
      servings.update(value.recipe.servings)
      note.update(value.recipe.note)
    }

    formRecipeState.chapterStates.clear()
    value.chapters.forEach { chapter ->
      formRecipeState.addChapter().apply {
        fields.apply {
          name.update(chapter.chapter.name)
          note.update(chapter.chapter.note)
        }
        chapter.steps.forEach { step ->
          addStep().apply {
            fields.apply {
              description.update(step.step.description)
              timerMinutes.update(step.step.timerMinutes)
              note.update(step.step.note)
            }
            step.ingredients.forEach { ingredient ->
              addIngredient().apply {
                fields.apply {
                  name.update(ingredient.name)
                  amount.update(ingredient.amount)
                  unit.update(ingredient.unit)
                }
              }
            }
          }
        }
      }
    }
    savingState = SavingState()
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )
  val categorySuggestions = flow { emit(fetchCategorySuggestions()) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyMap()
  )

  val formRecipeState =
    FormRecipeState(onCanSaveChanged = { canSave = canSave() }, onChange = { onChange() })
  var isNew by mutableStateOf(true)
    private set
  var savingState by mutableStateOf(SavingState())
    private set
  var canSave by mutableStateOf(canSave())
    private set

  fun deleteRecipe() {
    viewModelScope.launch {
      runCatching {
        val recipe = checkNotNull(recipe.value)
        deleteData(recipe)
      }
    }
  }

  fun saveRecipe() {
    viewModelScope.launch {
      runCatching {
        check(canSave())

        val recipe = checkNotNull(recipe.value).recipe
        val data = formRecipeState.let { r ->
          RecipeFormInfoFields(
            name = r.fields.name.value,
            category = r.fields.category.value,
            subCategory = r.fields.subCategory.value,
            servings = r.fields.servings.value ?: 0,
            note = r.fields.note.value,
            chapters = r.chapterStates.values.map { c ->
              RecipeFormChapterFields(
                name = c.fields.name.value,
                note = c.fields.note.value,
                steps = c.steps.values.map { s ->
                  RecipeFormStepFields(
                    uuid = s.id,
                    description = s.fields.description.value,
                    timerMinutes = s.fields.timerMinutes.value,
                    note = s.fields.note.value,
                    ingredients = s.ingredients.values.map { i ->
                      RecipeFormIngredientFields(
                        uuid = i.id,
                        name = i.fields.name.value,
                        amount = i.fields.amount.value,
                        unit = i.fields.unit.value
                      )
                    })
                })
            })
        }

        savingState = savingState.getAsSaving()
        saveData(data, recipe.id, recipe.thumbnailUri)
        savingState = savingState.getAsSaved()
      }
    }
  }

  private fun onChange() {
    savingState = savingState.copy(unsavedChanges = true)
  }

  private fun canSave(): Boolean {
    if (savingState.state == SavingState.State.SAVING) return false
    return formRecipeState.canSave.value
  }
}

fun NavGraphBuilder.recipeFormPage(
  onOpenRecipe: (id: Long) -> Unit,
  onOpenCategories: () -> Unit,
  onSnackbar: (SnackbarProperties) -> Unit,
  onBack: () -> Unit
) {
  composable<RecipeFormPage> { navStack ->
    val (recipeId) = navStack.toRoute<RecipeFormPage>()
    val recipeDeletedMessage = stringResource(R.string.snackbar_recipe_deleted_successfully)
    val appContext = LocalContext.current.applicationContext
    val dao = RecipeDatabase.getDatabase(appContext).recipeDao()
    val viewmodel: RecipeFormViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormViewModel(
          fetchData = { fetchRecipe(dao = dao, recipeId = recipeId) },
          saveData = { fields, id, thumbnail ->
            saveRecipe(
              dao = dao, id = id, thumbnailUri = thumbnail, fields = fields
            ).also { result -> onOpenRecipe(result) }
          },
          deleteData = { recipe ->
            deleteRecipe(
              dao = dao, photoService = PhotoService(context = appContext), recipe = recipe.recipe
            ).onTrue {
              onSnackbar(SnackbarProperties(recipeDeletedMessage))
              onOpenCategories()
            }
          },
          fetchCategorySuggestions = { fetchCategorySuggestions(recipeDao = dao) },
        )
      }
    })

    val recipe by viewmodel.recipe.collectAsStateWithLifecycle()
    val categorySuggestions by viewmodel.categorySuggestions.collectAsStateWithLifecycle()

    LoadingScreen(loading = recipe == null) {
      RecipeFormContent(
        isNew = viewmodel.isNew,
        formRecipeState = viewmodel.formRecipeState,
        categorySuggestions = categorySuggestions,
        savingState = viewmodel.savingState,
        canSave = viewmodel.canSave,
        onBack = onBack,
        onSubmit = { viewmodel.saveRecipe() },
        onDelete = { viewmodel.deleteRecipe() },
      )
    }
  }
}

@Composable
private fun RecipeFormContent(
  isNew: Boolean,
  formRecipeState: FormRecipeState,
  categorySuggestions: Map<String, List<String>>,
  savingState: SavingState,
  canSave: Boolean,
  onBack: () -> Unit,
  onSubmit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
  initialPage: Int = 0,
) {
  val pagerState = rememberPagerState(
    initialPage = initialPage, pageCount = { 1 + formRecipeState.chapterStates.values.size })

  var animateToPageTarget by remember { mutableStateOf<Int?>(null) }
  var openUnsavedDialog by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(animateToPageTarget) {
    val target = animateToPageTarget
    if (target != null) pagerState.animateScrollToPage(page = target)
    animateToPageTarget = null
  }

  UnsavedDialog(
    open = openUnsavedDialog,
    onDismiss = { openUnsavedDialog = false },
    onConfirm = {
      openUnsavedDialog = false
      onBack()
    },
  )

  BackHandler(enabled = savingState.unsavedChanges) {
    openUnsavedDialog = true
  }

  Scaffold(topBar = {
    PrimaryTopAppBar(
      title = when (isNew) {
        true -> stringResource(R.string.screen_title_new_recipe)
        else -> stringResource(R.string.screen_title_edit_recipe)
      },
      onBack = {
        if (savingState.unsavedChanges) openUnsavedDialog = true
        else onBack()
      },
      actions = {
        IconButton(onClick = onSubmit, enabled = canSave) {
          Icon(
            painter = painterResource(R.drawable.rounded_check_24),
            contentDescription = stringResource(R.string.cd_save)
          )
        }
      },
    )
  }, floatingActionButton = {
    ExtendedFloatingActionButton(
      onClick = {
        formRecipeState.addChapter()
        animateToPageTarget = pagerState.pageCount + 1
      },
      icon = {
        Icon(
          painter = painterResource(R.drawable.rounded_add_24),
          contentDescription = stringResource(R.string.cd_add_new_chapter)
        )
      },
      text = { Text(text = stringResource(R.string.btn_add_chapter)) },
      modifier = Modifier.padding(bottom = 24.dp),
    )
  }) { paddingValues ->
    BackgroundSurface(
      modifier
        .padding(paddingValues)
        .fillMaxSize()
    ) {
      HorizontalPager(
        state = pagerState, verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxSize()
      ) { pageIndex ->
        when (pageIndex) {
          0 -> RecipeInfoScreen(
            formState = formRecipeState,
            categorySuggestions = categorySuggestions,
            onDelete = ifElse(condition = isNew, ifTrue = { null }, ifFalse = { { onDelete() } }),
            modifier = Modifier.padding(8.dp),
          )

          else -> {
            val index = pageIndex - 1
            val formState = formRecipeState.chapterStates.values.elementAtOrNull(index = index)

            if (formState != null) {
              RecipeChapterScreen(
                index = index,
                formState = formState,
                onDelete = {
                  formRecipeState.chapterStates.removeAt(index)
                  animateToPageTarget = pageIndex - 1
                },
                onMoveLeft = if (index != 0) {
                  {
                    formRecipeState.chapterStates.swapAt(index, index - 1)
                    animateToPageTarget = pageIndex - 1
                  }
                }
                else null,
                onMoveRight = if (index != formRecipeState.chapterStates.values.size - 1) {
                  {
                    formRecipeState.chapterStates.swapAt(index, index + 1)
                    animateToPageTarget = pageIndex + 1
                  }
                }
                else null,
                modifier = Modifier.padding(horizontal = 8.dp),
              )
            }
          }
        }
      }
      Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
          .wrapContentHeight()
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(bottom = 8.dp),
      ) {
        repeat(pagerState.pageCount) { i ->
          Box(
            modifier = Modifier
              .padding(2.dp)
              .clip(CircleShape)
              .background(
                if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHighest
              )
              .size(16.dp)
              .clickable { animateToPageTarget = i })
        }
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun Preview() {
  CookbookTheme(useDarkTheme = true) {
    RecipeFormContent(
      isNew = false,
      formRecipeState = FormRecipeState(onCanSaveChanged = {}).also {
        it.fields.apply {
          name.update("Recipe 1")
          category.update("Cat 1")
          subCategory.update("Sub 1")
          note.update("This is a note")
        }
        it.chapterStates.add(
          FormChapterState(onCanSaveChanged = {}),
          FormChapterState(onCanSaveChanged = {}),
          FormChapterState(onCanSaveChanged = {}),
        )
      },
      categorySuggestions = emptyMap(),
      savingState = SavingState(),
      canSave = true,
      onBack = {},
      onSubmit = {},
      onDelete = {},
      initialPage = 0,
    )
  }
}