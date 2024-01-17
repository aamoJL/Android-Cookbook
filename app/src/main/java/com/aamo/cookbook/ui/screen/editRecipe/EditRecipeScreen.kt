package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aamo.cookbook.R
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@Composable
fun EditRecipeScreen(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier,
  onEditChapter: (ChapterWithStepsAndIngredients?) -> Unit = {},
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
    modifier = modifier
  )
}

@Composable
fun EditRecipeScreenPageContent(
  uiState: EditRecipeViewModel.InfoScreenUiState,
  modifier: Modifier = Modifier,
  onFormStateChange: (EditRecipeViewModel.InfoScreenUiState.InfoFormState) -> Unit = {},
  onEditChapter: (ChapterWithStepsAndIngredients?) -> Unit = {},
  onDeleteChapter: (ChapterWithStepsAndIngredients) -> Boolean = { false },
  onSubmitChanges: () -> Unit = {},
  onDelete: () -> Unit = {},
  onBack: () -> Unit = {},
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
      BasicTopAppBar(title = when (uiState.id) {
        0 -> stringResource(R.string.screen_title_new_recipe)
        else -> stringResource(R.string.screen_title_existing_recipe)
      }, onBack = {
        if (uiState.unsavedChanges) openUnsavedDialog = true
        else onBack()
      }, actions = {
        if (uiState.id != 0) {
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
        onDeleteChapter = onDeleteChapter
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
        label = stringResource(R.string.textfield_recipe_subcategory),
        options = subCategorySuggestions
      )
    }
  }
}

@Composable
private fun ChapterList(
  chapters: List<ChapterWithStepsAndIngredients>,
  onEditChapter: (ChapterWithStepsAndIngredients?) -> Unit,
  onDeleteChapter: (ChapterWithStepsAndIngredients) -> (Boolean),
  modifier: Modifier = Modifier
) {
  FormList(
    title = stringResource(R.string.form_list_title_chapters),
    onAddClick = { onEditChapter(null) },
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = chapters,
        key = { _, chapter -> chapter.hashCode()}
      ){index, chapter ->
        ChapterListItem(
          chapter = chapter,
          chapterNumber = index + 1,
          onClick = { onEditChapter(chapter) },
          onDismiss = { onDeleteChapter(chapter) },
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .fillMaxWidth()
        )
        Divider()
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
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(Tags.CHAPTER_ITEM.name),
      headlineContent = {
        Column(
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = modifier.fillMaxWidth()
        ) {
          Text(
            text = "${chapterNumber}. ${chapter.value.name}",
            style = MaterialTheme.typography.titleMedium
          )
          Column(
            modifier = Modifier
              .padding(start = 16.dp)
              .width(IntrinsicSize.Max)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              for ((index, step) in chapter.steps.withIndex()) {
                Column {
                  Text(
                    text = "${index + 1}. ${step.value.getDescriptionWithFormattedEndChar(step.ingredients.isEmpty())}",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Column(modifier = Modifier.padding(start = 16.dp)) {
                    for (ingredient in step.ingredients) {
                      Row {
                        Text(
                          text = if (ingredient.amount == 0f) "" else ingredient.amount.toFractionFormattedString(),
                          style = MaterialTheme.typography.bodySmall,
                          textAlign = TextAlign.End,
                          modifier = Modifier
                            .defaultMinSize(minWidth = 40.dp)
                            .weight(1f)
                        )
                        Text(
                          text = ingredient.unit,
                          style = MaterialTheme.typography.bodySmall,
                          fontStyle = FontStyle.Italic,
                          modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                        )
                        Text(
                          text = ingredient.name,
                          style = MaterialTheme.typography.bodySmall,
                          modifier = Modifier.weight(5f)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    )
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextFieldWithOptions(
  value: String,
  label: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  imeAction: ImeAction = ImeAction.Next,
  options: List<String> = emptyList(),
) {
  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded },
    modifier = modifier,
  ) {
    FormTextField(
      // The `menuAnchor` modifier must be passed to the text field for correctness.
      modifier = Modifier.menuAnchor(),
      value = value,
      onValueChange = onValueChange,
      label = label,
      imeAction = imeAction,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    // filter options based on text field value
    val filteringOptions = options.filter { it.contains(value, ignoreCase = true) }
    if (filteringOptions.isNotEmpty()) {
      DropdownMenu(
        modifier = Modifier
          .background(Color.White)
          .exposedDropdownSize(true),
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
      ) {
        filteringOptions.forEach { selectionOption ->
          DropdownMenuItem(
            text = { Text(selectionOption) },
            onClick = {
              onValueChange(selectionOption)
              expanded = false
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }
    }
  }
}

