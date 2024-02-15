package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aamo.cookbook.R
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.FormTextFieldDefaults
import com.aamo.cookbook.ui.components.form.FormTextFieldWithOptions
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.asOptionalLabel
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import java.util.UUID

@Composable
fun EditRecipeScreen(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier,
  onEditChapter: (index: Int) -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onDelete: () -> Unit = {},
  onBack: () -> Unit = {}
) {
  val uiState by viewModel.infoUiState.collectAsStateWithLifecycle()

  EditRecipeScreenPageContent(
    uiState = uiState,
    onFormStateChange = { viewModel.setInfoFormState(it) },
    onDeleteChapter = { viewModel.deleteChapter(it) },
    onEditChapter = onEditChapter,
    onSubmitChanges = onSubmitChanges,
    onDelete = onDelete,
    onBack = onBack,
    onSwapChapters = { from, to -> viewModel.swapChapterPositions(from, to) },
    modifier = modifier
  )
}

@Composable
fun EditRecipeScreenPageContent(
  uiState: EditRecipeViewModel.InfoScreenUiState,
  modifier: Modifier = Modifier,
  onFormStateChange: (EditRecipeViewModel.InfoScreenUiState.InfoFormState) -> Unit = {},
  onEditChapter: (index: Int) -> Unit = {},
  onDeleteChapter: (index: Int) -> Boolean = { false },
  onSubmitChanges: () -> Unit = {},
  onDelete: () -> Unit = {},
  onBack: () -> Unit = {},
  onSwapChapters: (from: Int, to: Int) -> Unit = {_,_ -> }
) {
  var openUnsavedDialog by remember { mutableStateOf(false) }
  var openDeleteDialog by remember { mutableStateOf(false) }

  if (openUnsavedDialog) {
    UnsavedDialog(
      onDismiss = { openUnsavedDialog = false },
      onConfirm = {
        openUnsavedDialog = false
        onBack()
      })
  } else if (openDeleteDialog) {
    AlertDialog(
      title = { Text(text = stringResource(R.string.dialog_title_delete_recipe)) },
      text = { Text(text = stringResource(R.string.dialog_text_delete_recipe)) },
      onDismissRequest = { openDeleteDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            openDeleteDialog = false
            onDelete()
          },
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
          Text(text = stringResource(R.string.dialog_confirm_delete_recipe))
        }
      },
      dismissButton = {
        TextButton(onClick = { openDeleteDialog = false }) {
          Text(text = stringResource(R.string.dialog_dismiss_default))
        }
      },
    )
  }

  BackHandler(true) {
    if (uiState.unsavedChanges) openUnsavedDialog = true
    else onBack()
  }

  Scaffold(
    topBar = {
      BasicTopAppBar(title = when (uiState.isNewRecipe) {
        true -> stringResource(R.string.screen_title_new_recipe)
        else -> stringResource(R.string.screen_title_existing_recipe)
      }, onBack = {
        if (uiState.unsavedChanges) openUnsavedDialog = true
        else onBack()
      }, actions = {
        if (!uiState.isNewRecipe) {
          IconButton(onClick = { openDeleteDialog = true }) {
            Icon(
              imageVector = Icons.Filled.Delete,
              contentDescription = stringResource(R.string.description_delete_recipe)
            )
          }
        }
        IconButton(onClick = onSubmitChanges, enabled = uiState.canBeSaved) {
          Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = stringResource(R.string.description_save_recipe)
          )
        }
      })
    }
  ) {
    Column(
      modifier = modifier
        .padding(it)
        .padding(8.dp)
    ) {
      InfoForm(
        uiState = uiState.formState,
        onStateChange = onFormStateChange,
        categorySuggestions = uiState.categorySuggestions.map { tuple -> tuple.category }
          .distinct(),
        subCategorySuggestions = uiState.categorySuggestions.filter { tuple -> tuple.category == uiState.formState.category && tuple.subCategory.isNotEmpty() }
          .map { filtered -> filtered.subCategory }.distinct()
      )
      Spacer(modifier = Modifier.padding(8.dp))
      ChapterList(
        chapters = uiState.chapters,
        onEditChapter = onEditChapter,
        onDeleteChapter = onDeleteChapter,
        onSwap = onSwapChapters
      )
    }
  }
}

@Composable
private fun InfoForm(
  uiState: EditRecipeViewModel.InfoScreenUiState.InfoFormState,
  modifier: Modifier = Modifier,
  categorySuggestions: List<String> = emptyList(),
  subCategorySuggestions: List<String> = emptyList(),
  onStateChange: (EditRecipeViewModel.InfoScreenUiState.InfoFormState) -> Unit = {}
) {
  FormBase(title = stringResource(R.string.form_title_recipe), modifier = modifier) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FormTextField(
        value = uiState.name,
        onValueChange = { onStateChange(uiState.copy(name = it)) },
        label = stringResource(R.string.textfield_recipe_name),
        modifier = Modifier.weight(2f, true)
      )
      FormNumberField(
        value = uiState.servings,
        onValueChange = { onStateChange(uiState.copy(servings = it)) },
        label = stringResource(R.string.textfield_recipe_servings),
        modifier = Modifier.weight(1f, true)
      )
    }
    Column(
      verticalArrangement = Arrangement.spacedBy(5.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FormTextFieldWithOptions(
        value = uiState.category,
        onValueChange = { onStateChange(uiState.copy(category = it)) },
        label = stringResource(R.string.textfield_recipe_category),
        options = categorySuggestions,
      )
      FormTextFieldWithOptions(
        value = uiState.subCategory,
        onValueChange = { onStateChange(uiState.copy(subCategory = it)) },
        label = stringResource(R.string.textfield_recipe_subcategory).asOptionalLabel(),
        options = subCategorySuggestions
      )
      FormTextField(
        value = uiState.note,
        onValueChange = { onStateChange(uiState.copy(note = it))},
        label = stringResource(R.string.textfield_label_note).asOptionalLabel(),
        keyboardOptions = FormTextFieldDefaults.keyboardOptions.copy(
          imeAction = ImeAction.Done
        ),
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChapterList(
  chapters: List<Pair<UUID, ChapterWithStepsAndIngredients>>,
  onEditChapter: (index: Int) -> Unit,
  onDeleteChapter: (index: Int) -> Boolean,
  onSwap: (from: Int, to: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  FormList(
    title = stringResource(R.string.form_list_title_chapters),
    onAddClick = { onEditChapter(-1) },
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = chapters,
        key = { _, pair -> pair.first }
      ) { index, pair ->
        Column(modifier = Modifier.animateItemPlacement()) {
          ChapterListItem(
            chapter = pair.second,
            chapterNumber = index + 1,
            onClick = { onEditChapter(index) },
            onDismiss = { onDeleteChapter(index) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            } else null,
            onMoveDown = if (index != chapters.size - 1) {
              { onSwap(index, index + 1) }
            } else null,
            modifier = Modifier.fillMaxWidth()
          )
          Divider()
        }
      }
    }
  }
}

@Composable
private fun ChapterListItem(
  chapter: ChapterWithStepsAndIngredients,
  chapterNumber: Int,
  onClick: () -> Unit,
  onDismiss: () -> (Boolean),
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss, modifier = modifier) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(Tags.CHAPTER_ITEM.name),
      headlineContent = {
        Text(
          text = "${chapterNumber}. ${chapter.value.name}",
          style = MaterialTheme.typography.titleMedium
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
              if(step.value.timerMinutes != null) {
                Text(
                  text = stringResource(
                    R.string.minutes_amount_abbreviation, step.value.timerMinutes.toString()
                  ),
                  style = MaterialTheme.typography.labelSmall
                )
              }
              Text(
                text = "${index + 1}. ${step.value.getDescriptionWithFormattedEndChar(step.ingredients.isEmpty())}",
                style = MaterialTheme.typography.bodyMedium
              )
              IngredientList(
                ingredients = step.ingredients,
                modifier = Modifier.padding(start = 16.dp)
              )
            }
          }
        }
      },
      trailingContent = {
        Column(modifier = Modifier) {
          if (onMoveUp != null) IconButton(onClick = onMoveUp) {
            Icon(
              imageVector = Icons.Filled.KeyboardArrowUp,
              contentDescription = stringResource(R.string.description_move_up)
            )
          }
          if (onMoveDown != null) IconButton(onClick = onMoveDown) {
            Icon(
              imageVector = Icons.Filled.KeyboardArrowDown,
              contentDescription = stringResource(R.string.description_move_down)
            )
          }
        }
      }
    )
  }
}

@Composable
private fun IngredientList(
  ingredients: List<Ingredient>,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
      ingredients.forEach {
        Text(
          text = if (it.amount == 0f) "" else it.amount.toFractionFormattedString(),
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
      ingredients.forEach {
        Text(
          text = it.unit,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
        )
      }
    }
    Column {
      ingredients.forEach {
        Text(
          text = it.name,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
        )
      }
    }
  }
}

