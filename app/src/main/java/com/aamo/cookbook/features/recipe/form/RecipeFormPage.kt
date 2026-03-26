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
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel.FormInfoState
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
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
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.getNewUUID
import com.aamo.cookbook.utility.extensions.general.ifElse
import com.aamo.cookbook.utility.extensions.general.onTrue
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RecipeFormPage(val id: Long)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val saveData: suspend (fields: RecipeFormInfoFields, id: Long, thumbnail: String) -> Unit,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
  private val fetchCategorySuggestions: suspend () -> Map<String, List<String>>,
) : ViewModel() {
  class FormInfoState(onChange: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val category = ViewModelState(String.EMPTY).onChange { onChange() }
    val subCategory = ViewModelState(String.EMPTY).onChange { onChange() }
    val servings = ViewModelState<Int?>(1).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange { onChange() }
    val note = ViewModelState(String.EMPTY).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }
    var noteFieldToggleValue by mutableStateOf(false)

    init {
      noteFieldToggleValue = note.value.isNotEmpty()
    }

    fun canSave(): Boolean {
      if (name.value.isEmpty()) return false
      if (category.value.isEmpty()) return false
      servings.value.also { if (it == null || it < 1) return false }
      return true
    }
  }

  class FormChapterState(val onChange: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val note = ViewModelState(String.EMPTY).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }
    val steps = ViewModelStateList<FormStepState>().onChange { onChange() }
    var noteFieldToggleValue by mutableStateOf(false)
    var selectedStepId by mutableStateOf<UUID?>(null)

    init {
      noteFieldToggleValue = note.value.isNotEmpty()
    }

    fun addStep(): FormStepState {
      return FormStepState(
        id = getNewUUID(used = steps.values.map { it.id }), onChange = onChange
      ).also {
        steps.add(it)
      }
    }

    fun canSave(): Boolean {
      if (name.value.isEmpty()) return false
      if (steps.values.isEmpty()) return false
      return true
    }
  }

  class FormStepState(val id: UUID, val onChange: () -> Unit) {
    val description = ViewModelState(String.EMPTY).onChange { onChange() }
    val timerMinutes = ViewModelState<Int?>(null).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange {
      if (it != null && it > 0) timerFieldToggleValue = true
      onChange()
    }
    val note = ViewModelState(String.EMPTY).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }
    val ingredients = ViewModelStateList<FormIngredientState>().onChange { onChange() }
    var noteFieldToggleValue by mutableStateOf(false)
    var timerFieldToggleValue by mutableStateOf(false)

    init {
      noteFieldToggleValue = note.value.isNotEmpty()
      timerFieldToggleValue = timerMinutes.value?.let { it > 0 } ?: false
    }

    fun addIngredient(): FormIngredientState {
      return FormIngredientState(
        id = getNewUUID(ingredients.values.map { it.id }), onChange = onChange
      ).also {
        ingredients.add(it)
      }
    }

    fun canSave(): Boolean {
      if (description.value.isEmpty()) return false
      if (timerMinutes.value?.let { it < 0 } == true) return false
      return true
    }
  }

  class FormIngredientState(val id: UUID, onChange: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val amount = ViewModelState<Double?>(null).transformation { value ->
      if (value != null && value <= 0) null else value
    }.onChange { onChange() }
    val unit = ViewModelState(String.EMPTY).onChange { onChange() }

    fun canSave(): Boolean {
      if (name.value.isEmpty()) return false
      if (amount.value?.let { it < 0 } == true) return false
      return true
    }
  }

  val recipe = flow { emit(fetchData()) }.onEach { value ->
    isNew = value.recipe.id == 0L

    formInfoState.apply {
      name.update(value.recipe.name)
      category.update(value.recipe.category)
      subCategory.update(value.recipe.subCategory)
      servings.update(value.recipe.servings)
      note.update(value.recipe.note)
    }

    formChaptersStates.clear()
    formChaptersStates.add(*value.chapters.map { chapter ->
      FormChapterState(onChange = { onUnsavedChanges() }).apply {
        name.update(chapter.chapter.name)
        note.update(chapter.chapter.name)

        chapter.steps.forEach { step ->
          addStep().apply {
            description.update(step.step.description)
            timerMinutes.update(step.step.timerMinutes)
            note.update(step.step.note)

            step.ingredients.forEach { ingredient ->
              addIngredient().apply {
                name.update(ingredient.name)
                amount.update(ingredient.amount)
                unit.update(ingredient.unit)
              }
            }
          }
        }
      }
    }.toTypedArray())

    savingState = SavingState()
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )
  val categorySuggestions = flow { emit(fetchCategorySuggestions()) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyMap()
  )

  val formInfoState = FormInfoState(onChange = { onUnsavedChanges() })
  val formChaptersStates = ViewModelStateList<FormChapterState>().onChange { onUnsavedChanges() }
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
        val data = RecipeFormInfoFields(
          name = formInfoState.name.value,
          category = formInfoState.category.value,
          subCategory = formInfoState.subCategory.value,
          servings = formInfoState.servings.value ?: 0,
          note = formInfoState.note.value,
          chapters = formChaptersStates.values.map { c ->
            RecipeFormChapterFields(
              name = c.name.value,
              note = c.note.value,
              steps = c.steps.values.map { s ->
                RecipeFormStepFields(
                  uuid = s.id,
                  description = s.description.value,
                  timerMinutes = s.timerMinutes.value,
                  note = s.note.value,
                  ingredients = s.ingredients.values.map { i ->
                    RecipeFormIngredientFields(
                      uuid = i.id, name = i.name.value, amount = i.amount.value, unit = i.unit.value
                    )
                  })
              })
          })

        saveData(data, recipe.id, recipe.thumbnailUri)
      }
    }
  }

  fun addChapter() {
    formChaptersStates.add(FormChapterState(onChange = { onUnsavedChanges() }))
  }

  fun removeChapterAt(index: Int) {
    formChaptersStates.removeAt(index)
  }

  fun swapChapters(from: Int, to: Int) {
    formChaptersStates.swapAt(from, to)
  }

  private fun onUnsavedChanges() {
    savingState = savingState.copy(unsavedChanges = true)
    canSave = canSave()
  }

  private fun canSave(): Boolean {
    if (savingState.state == SavingState.State.SAVING) return false
    if (!formInfoState.canSave()) return false
    if (formChaptersStates.values.isEmpty()) return false
    return formChaptersStates.values.all { c ->
      c.canSave() && c.steps.values.all { s ->
        s.canSave() && s.ingredients.values.all { i ->
          i.canSave()
        }
      }
    }
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
        formInfoState = viewmodel.formInfoState,
        formChapterStates = viewmodel.formChaptersStates.values,
        categorySuggestions = categorySuggestions,
        savingState = viewmodel.savingState,
        canSave = viewmodel.canSave,
        onBack = onBack,
        onSubmit = { viewmodel.saveRecipe() },
        onDelete = { viewmodel.deleteRecipe() },
        onAddChapter = { viewmodel.addChapter() },
        onDeleteChapter = { i -> viewmodel.removeChapterAt(i) },
        onSwapChapters = { from, to -> viewmodel.swapChapters(from, to) })
    }
  }
}

@Composable
private fun RecipeFormContent(
  isNew: Boolean,
  formInfoState: FormInfoState,
  formChapterStates: List<RecipeFormViewModel.FormChapterState>,
  categorySuggestions: Map<String, List<String>>,
  savingState: SavingState,
  canSave: Boolean,
  onBack: () -> Unit,
  onSubmit: () -> Unit,
  onDelete: () -> Unit,
  onAddChapter: () -> Unit,
  onDeleteChapter: (index: Int) -> Unit,
  onSwapChapters: (from: Int, to: Int) -> Unit,
  modifier: Modifier = Modifier,
  initialPage: Int = 0,
) {
  val pagerState =
    rememberPagerState(initialPage = initialPage, pageCount = { 1 + formChapterStates.size })

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
        onAddChapter()
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
            formState = formInfoState,
            categorySuggestions = categorySuggestions,
            onDelete = ifElse(condition = isNew, ifTrue = { null }, ifFalse = { { onDelete() } }),
            modifier = Modifier.padding(8.dp),
          )

          else -> {
            val index = pageIndex - 1
            val formState = formChapterStates.elementAtOrNull(index = index)

            if (formState != null) {
              RecipeChapterScreen(
                index = index,
                formState = formState,
                onDelete = {
                  onDeleteChapter(index)
                  animateToPageTarget = pageIndex - 1
                },
                onMoveLeft = if (index != 0) {
                  {
                    onSwapChapters(index, index - 1)
                    animateToPageTarget = pageIndex - 1
                  }
                }
                else null,
                onMoveRight = if (index != formChapterStates.size - 1) {
                  {
                    onSwapChapters(index, index + 1)
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
      formInfoState = FormInfoState(onChange = {}).apply {
        name.update("Recipe 1")
        category.update("Cat 1")
        subCategory.update("Sub 1")
        note.update("This is a note")
      },
      formChapterStates = listOf(
        RecipeFormViewModel.FormChapterState(onChange = {}).apply {
          name.update("Chapter 1")
          note.update("This is a note")
          steps.add(
            RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange).apply {
              description.update("This is a description")
              timerMinutes.update(4)
              note.update("This is a note")
              ingredients.add(
                RecipeFormViewModel.FormIngredientState(
                id = UUID.randomUUID(), onChange
              ).apply {
                name.update("Ingredient 1")
                amount.update(20.0)
                unit.update("g")
              }, RecipeFormViewModel.FormIngredientState(id = UUID.randomUUID(), onChange).apply {
                name.update("Ingredient 2")
                amount.update(200.0)
                unit.update("mg")
              })
            },
            RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange),
          )
        },
        RecipeFormViewModel.FormChapterState(onChange = {}),
        RecipeFormViewModel.FormChapterState(onChange = {}),
      ),
      categorySuggestions = emptyMap(),
      savingState = SavingState(),
      canSave = true,
      onBack = {},
      onSubmit = {},
      onDelete = {},
      onAddChapter = {},
      onDeleteChapter = {},
      onSwapChapters = { _, _ -> },
      initialPage = 0,
    )
  }
}