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
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.states.FormChapterState
import com.aamo.cookbook.features.recipe.form.models.states.FormRecipeState
import com.aamo.cookbook.features.recipe.form.screens.RecipeChapterScreen
import com.aamo.cookbook.features.recipe.form.screens.RecipeInfoScreen
import com.aamo.cookbook.features.recipe.form.use_cases.copyRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.deleteThumbnail
import com.aamo.cookbook.features.recipe.form.use_cases.fetchCategorySuggestions
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.saveThumbnail
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.ifElse
import com.aamo.cookbook.utility.extensions.general.letIf
import com.aamo.cookbook.utility.extensions.general.onTrue
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormPage(val id: Long, val asCopy: Boolean = false)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val saveData: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Boolean,
  private val deleteThumbnail: (String) -> Unit,
  private val saveThumbnail: (String) -> String?,
  private val fetchCategorySuggestions: suspend () -> Map<String, List<String>>,
) : ViewModel() {
  val recipe = flow { emit(fetchData()) }.onEach { model ->
    isNew = model.recipe.id == 0L
    formRecipeState.update(createRecipeState(model = model))
    savingState = SavingState()
    validity = checkValidity()
  }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)
  val categorySuggestions = flow { emit(fetchCategorySuggestions()) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyMap()
  )

  val formRecipeState =
    ViewModelState(createRecipeState(model = RecipeWithChaptersStepsAndIngredients(recipe = Recipe()))).onChange {
      savingState = SavingState()
    }
  var isNew by mutableStateOf(true)
    private set
  var savingState by mutableStateOf(SavingState())
    private set
  var validity by mutableStateOf(checkValidity())
    private set

  fun deleteRecipe() {
    viewModelScope.launch {
      runCatching {
        val recipe = checkNotNull(recipe.value)
        deleteData(recipe).onTrue {
          if (recipe.recipe.thumbnailUri.isNotEmpty()) {
            deleteThumbnail(recipe.recipe.thumbnailUri)
          }
        }
      }
    }
  }

  fun saveRecipe() {
    viewModelScope.launch {
      runCatching {
        val recipe = recipe.value
        val newThumbnail = formRecipeState.value.fields.thumbnailUri.value
        check(checkValidity())
        checkNotNull(recipe)

        savingState = savingState.getAsSaving()

        if (recipe.recipe.thumbnailUri != newThumbnail) {
          if (recipe.recipe.thumbnailUri.isNotEmpty()) {
            // delete old thumbnail
            deleteThumbnail(recipe.recipe.thumbnailUri)
          }
          if (newThumbnail.endsWith(PhotoService.TEMP_FILE_EXTENSION)) {
            // save new thumbnail
            saveThumbnail(newThumbnail)?.also { newThumbnail ->
              formRecipeState.value.fields.thumbnailUri.update(newThumbnail)
            }
          }
        }

        saveData(formRecipeState.value.getModel())

        savingState = savingState.getAsSaved()
      }.onFailure { savingState = savingState.getAsError(Error(it.localizedMessage)) }
    }
  }

  private fun createRecipeState(model: RecipeWithChaptersStepsAndIngredients): FormRecipeState {
    return FormRecipeState(
      model = model,
      onChange = { onChange() },
      onValidityChanged = { validity = checkValidity() },
      onThumbnailChanging = { old ->
        runCatching {
          if (old.endsWith(PhotoService.TEMP_FILE_EXTENSION)) {
            deleteThumbnail(old)
          }
        }
      },
    )
  }

  private fun onChange() {
    savingState = savingState.copy(unsavedChanges = true)
  }

  private fun checkValidity(): Boolean {
    if (savingState.state == SavingState.State.SAVING) return false
    return formRecipeState.value.validity.value
  }

  override fun onCleared() {
    super.onCleared()

    runCatching {
      formRecipeState.value.fields.thumbnailUri.value.also { thumbnailUri ->
        if (thumbnailUri.endsWith(PhotoService.TEMP_FILE_EXTENSION)) {
          deleteThumbnail(thumbnailUri)
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
    val (recipeId, asCopy) = navStack.toRoute<RecipeFormPage>()
    val recipeDeletedMessage = stringResource(R.string.snackbar_recipe_deleted_successfully)
    val appContext = LocalContext.current.applicationContext
    val dao = RecipeDatabase.getDatabase(appContext).recipeDao()
    val viewmodel: RecipeFormViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormViewModel(
          fetchData = {
            fetchRecipe(dao = dao, recipeId = recipeId).letIf(asCopy) {
              copyRecipe(recipe = it, context = appContext)
            }
          },
          saveData = { recipe ->
            saveRecipe(dao = dao, recipe = recipe).also { result -> onOpenRecipe(result) }
          },
          deleteData = { recipe ->
            deleteRecipe(dao = dao, recipe = recipe.recipe).onTrue {
              onSnackbar(SnackbarProperties(recipeDeletedMessage))
              onOpenCategories()
            }
          },
          deleteThumbnail = { fileName ->
            deleteThumbnail(fileName = fileName, photoService = PhotoService(context = appContext))
          },
          saveThumbnail = { tempFile ->
            saveThumbnail(
              tempName = tempFile,
              photoService = PhotoService(context = appContext),
              ioService = IOService(context = appContext),
            )
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
        formRecipeState = viewmodel.formRecipeState.value,
        categorySuggestions = categorySuggestions,
        savingState = viewmodel.savingState,
        canSave = viewmodel.validity,
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
      formRecipeState = FormRecipeState().also {
        it.fields.apply {
          name.update("Recipe 1")
          category.update("Cat 1")
          subCategory.update("Sub 1")
          note.update("This is a note")
        }
        it.chapterStates.add(
          FormChapterState(onValidityChanged = {}),
          FormChapterState(onValidityChanged = {}),
          FormChapterState(onValidityChanged = {}),
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