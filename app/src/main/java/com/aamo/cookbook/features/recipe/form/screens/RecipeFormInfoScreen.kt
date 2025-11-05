package com.aamo.cookbook.features.recipe.form.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.inputs.LoadingIconButton
import com.aamo.cookbook.ui.components.inputs.NullableIntNumberField
import com.aamo.cookbook.ui.components.inputs.OptionsTextField
import com.aamo.cookbook.ui.components.inputs.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.modals.DeleteDialog
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.onNotNull
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.tags.UITag
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object RecipeFormInfoScreen

class RecipeFormInfoScreenViewModel(
  private val formData: RecipeFormInfoFields,
  fetchCategorySuggestions: suspend () -> Map<String, List<String>>
) : ViewModel() {
  class FormState(formData: RecipeFormInfoFields) {
    val name = ViewModelState(formData.name).onChange { onUnsavedChanges() }
    val category = ViewModelState(formData.category).onChange { onUnsavedChanges() }
    val subCategory = ViewModelState(formData.category).onChange { onUnsavedChanges() }
    val servings = ViewModelState<Int?>(formData.servings).onChange { onUnsavedChanges() }
    val note = ViewModelState(formData.note).onChange { onUnsavedChanges() }
    val chapters = ViewModelStateList(formData.chapters).onChange { onUnsavedChanges() }
    var savingState by mutableStateOf(SavingState())

    fun canSave(): Boolean {
      if (savingState.state == SavingState.State.SAVING) return false
      if (name.value.isEmpty()) return false
      if (category.value.isEmpty()) return false
      if (chapters.values.isEmpty()) return false
      servings.value.also {
        if (it == null || it < 0) return false
      }
      return true
    }

    private fun onUnsavedChanges() {
      if (!savingState.unsavedChanges) {
        savingState = savingState.copy(unsavedChanges = true)
      }
    }
  }

  val formState = FormState(formData)
  val isNew = formData.name.isEmpty()

  var categorySuggestions by mutableStateOf<Map<String, List<String>>>(emptyMap())
    private set

  init {
    viewModelScope.launch {
      fetchCategorySuggestions().let { result ->
        categorySuggestions = result
      }
    }
  }

  fun update(chapter: RecipeFormChapterFields) {
    formState.chapters.values.indexOfFirst { it.uuid == chapter.uuid }.also { index ->
      if (index == -1) formState.chapters.add(chapter)
      else formState.chapters.replaceAt(index, chapter)
    }
  }

  fun saveModel(): RecipeFormInfoFields? {
    if (!formState.canSave()) return null

    formState.apply { savingState = savingState.getAsSaving() }

    return formState.let {
      formData.copy(
        name = it.name.value,
        category = it.category.value,
        subCategory = it.subCategory.value,
        servings = it.servings.value
          ?: throw IllegalArgumentException("Servings should not be null"),
        note = it.note.value,
        chapters = it.chapters.values
      )
    }.also {
      formState.apply { savingState = savingState.getAsSaved() }
    }
  }
}

fun NavGraphBuilder.recipeFormInfoScreen(
  formData: () -> RecipeFormInfoFields,
  onDeleteRecipe: () -> Unit,
  onSubmit: (RecipeFormInfoFields) -> Unit,
  onBack: () -> Unit
) {
  composable<RecipeFormInfoScreen> {
    val viewmodel: RecipeFormInfoScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormInfoScreenViewModel(formData = formData(), fetchCategorySuggestions = {
          emptyMap() // TODO()
        })
      }
    })

    val infoNavController = rememberNavController()

    NavHost(navController = infoNavController, startDestination = RecipeFormInfoScreen) {
      composable<RecipeFormInfoScreen> {
        RecipeFormInfoScreenContent(
          formState = viewmodel.formState,
          categorySuggestions = viewmodel.categorySuggestions,
          isNew = viewmodel.isNew,
          onNewChapter = {
            infoNavController.navigate(RecipeFormChapterScreen(index = viewmodel.formState.chapters.values.size))
          },
          onEditChapter = { infoNavController.navigate(RecipeFormChapterScreen(index = it)) },
          onDeleteChapter = { viewmodel.formState.chapters.remove(it) },
          onSwapChapters = { a, b -> viewmodel.formState.chapters.swapAt(a, b) },
          onDelete = onDeleteRecipe,
          onSubmit = { viewmodel.saveModel().onNotNull { onSubmit(it) } },
          onBack = onBack,
        )
      }
      recipeFormChapterScreen(formData = { index ->
        viewmodel.formState.chapters.values.elementAtOrElse(index) { RecipeFormChapterFields() }
      }, onSubmit = { chapter ->
        viewmodel.update(chapter)
        infoNavController.navigateUp()
      }, onBack = {
        infoNavController.navigateUp()
      })
    }
  }
}

@Composable
fun RecipeFormInfoScreenContent(
  formState: RecipeFormInfoScreenViewModel.FormState,
  categorySuggestions: Map<String, List<String>>,
  isNew: Boolean,
  onNewChapter: () -> Unit,
  onEditChapter: (index: Int) -> Unit,
  onDeleteChapter: (RecipeFormChapterFields) -> Unit,
  onSwapChapters: (from: Int, to: Int) -> Unit,
  onSubmit: () -> Unit,
  onDelete: () -> Unit,
  onBack: () -> Unit,
) {
  var openUnsavedDialog by remember { mutableStateOf(false) }
  var openDeleteDialog by remember { mutableStateOf(false) }

  UnsavedDialog(open = openUnsavedDialog, onDismiss = { openUnsavedDialog = false }, onConfirm = {
    openUnsavedDialog = false
    onBack()
  })

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_recipe),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onDelete()
    })

  BackHandler(enabled = formState.savingState.unsavedChanges) {
    openUnsavedDialog = true
  }

  Scaffold(
    topBar = {
      PrimaryTopAppBar(
        title = when (isNew) {
        true -> stringResource(R.string.screen_title_new_recipe)
        else -> stringResource(R.string.screen_title_existing_recipe)
      }, onBack = {
        if (formState.savingState.unsavedChanges) openUnsavedDialog = true
        else onBack()
      }, actions = {
        if (!isNew) {
          IconButton(onClick = { openDeleteDialog = true }) {
            Icon(
              painter = painterResource(R.drawable.rounded_delete_24),
              contentDescription = stringResource(R.string.cd_delete_recipe)
            )
          }
        }
        LoadingIconButton(
          onClick = onSubmit,
          isLoading = formState.savingState.state == SavingState.State.SAVING,
          enabled = formState.canSave(),
        ) {
          Icon(
            painter = painterResource(R.drawable.rounded_check_24),
            contentDescription = stringResource(R.string.cd_save)
          )
        }
      })
    }) {
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier
        .padding(it)
        .padding(8.dp)
    ) {
      InfoForm(formState = formState, categorySuggestions = categorySuggestions)
      ChapterList(
        chapters = formState.chapters.values,
        onNewChapter = onNewChapter,
        onEditChapter = onEditChapter,
        onDeleteChapter = onDeleteChapter,
        onSwap = onSwapChapters
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoForm(
  formState: RecipeFormInfoScreenViewModel.FormState,
  categorySuggestions: Map<String, List<String>>,
) {
  FormBase(title = stringResource(R.string.title_recipe)) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      TextField(
        value = formState.name.value,
        onValueChange = { formState.name.update(it) },
        label = { Text(stringResource(R.string.label_name)) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.weight(2f, true)
      )
      NullableIntNumberField(
        value = formState.servings.value,
        label = { Text(stringResource(R.string.label_servings)) },
        zeroEqualsNull = true,
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        onValueChange = { formState.servings.update(it) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.weight(1f, true)
      )
    }
    Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
      OptionsTextField(
        value = formState.category.value,
        label = { Text(stringResource(R.string.label_category)) },
        onValueChange = { formState.category.update(it) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        options = categorySuggestions.keys.filter {
          it.contains(formState.category.value, ignoreCase = true)
        }.sorted(),
        modifier = Modifier.fillMaxWidth()
      )
      OptionsTextField(
        value = formState.subCategory.value,
        label = { Text(stringResource(R.string.label_subcategory).asOptionalLabel()) },
        onValueChange = { formState.subCategory.update(it) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        options = categorySuggestions[formState.category.value]?.filter {
          it.contains(formState.subCategory.value, ignoreCase = true)
        }?.sorted() ?: emptyList(),
        modifier = Modifier.fillMaxWidth()
      )
      TextField(
        value = formState.note.value,
        onValueChange = { formState.note.update(it) },
        label = { Text(stringResource(R.string.label_note).asOptionalLabel()) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChapterList(
  chapters: List<RecipeFormChapterFields>,
  onNewChapter: () -> Unit,
  onEditChapter: (index: Int) -> Unit,
  onDeleteChapter: (RecipeFormChapterFields) -> Unit,
  onSwap: (from: Int, to: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  FormList(
    title = stringResource(R.string.title_chapters), onAddClick = onNewChapter, modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(items = chapters, key = { _, c -> c.uuid }) { index, chapter ->
        Column(modifier = Modifier.animateItem()) {
          ChapterListItem(
            chapter = chapter,
            chapterNumber = index + 1,
            onClick = { onEditChapter(index) },
            onDismiss = { onDeleteChapter(chapter) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            }
            else null,
            onMoveDown = if (index != chapters.size - 1) {
              { onSwap(index, index + 1) }
            }
            else null,
            modifier = Modifier.fillMaxWidth())

          if (index != chapters.size - 1) {
            HorizontalDivider()
          }
        }
      }
    }
  }
}

@Composable
private fun ChapterListItem(
  chapter: RecipeFormChapterFields,
  chapterNumber: Int,
  onClick: () -> Unit,
  onDismiss: () -> Unit,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss, modifier = modifier) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(UITag.CHAPTER_ITEM.name),
      headlineContent = {
        Text(
          text = "${chapterNumber}. ${chapter.name}", style = MaterialTheme.typography.titleMedium
        )
      },
      supportingContent = {
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .padding(start = 16.dp, top = 4.dp)
            .width(IntrinsicSize.Max)
        ) {
          chapter.steps.forEachIndexed { index, step ->
            Column {
              if (step.timerMinutes != null) {
                Text(
                  text = stringResource(
                    R.string.abbreviation_minutes, step.timerMinutes.toString()
                  ), style = MaterialTheme.typography.labelSmall
                )
              }
              Text(
                text = "${index + 1}. ${step.description}${if (step.ingredients.isEmpty()) "." else ":"}",
                style = MaterialTheme.typography.bodyMedium
              )
              ChapterListIngredientList(
                ingredients = step.ingredients, modifier = Modifier.padding(start = 16.dp)
              )
            }
          }
        }
      },
      trailingContent = {
        Column(modifier = Modifier) {
          if (onMoveUp != null) IconButton(onClick = onMoveUp) {
            Icon(
              painter = painterResource(R.drawable.rounded_keyboard_arrow_up_24),
              contentDescription = stringResource(R.string.cd_move_up)
            )
          }
          if (onMoveDown != null) IconButton(onClick = onMoveDown) {
            Icon(
              painter = painterResource(R.drawable.rounded_keyboard_arrow_down_24),
              contentDescription = stringResource(R.string.cd_move_down)
            )
          }
        }
      })
  }
}

@Composable
private fun ChapterListIngredientList(
  ingredients: List<RecipeFormIngredientFields>, modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
      ingredients.forEach {
        Text(
          text = if (it.amount == 0f || it.amount == null) "" else it.amount.toFractionFormattedString(),
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
      ingredients.forEach {
        Text(text = it.unit, style = MaterialTheme.typography.bodySmall, modifier = Modifier)
      }
    }
    Column {
      ingredients.forEach {
        Text(text = it.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier)
      }
    }
  }
}