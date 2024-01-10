package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier,
  onEditChapter: (ChapterWithStepsAndIngredients) -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onDelete: () -> Unit = {},
  onBack: () -> Unit = {}
) {
  val uiState by viewModel.infoUiState.collectAsState()
  val formIsValid by remember(uiState) {
    mutableStateOf(formValidation(uiState))
  }
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
    if (uiState.unsavedChanges) {
      openUnsavedDialog = true
    } else {
      onBack()
    }
  }

  Scaffold(
    topBar = {
      BasicTopAppBar(title = when (uiState.id) {
        0 -> stringResource(R.string.screen_title_new_recipe)
        else -> stringResource(R.string.screen_title_existing_recipe)
      }, onBack = {
        if (uiState.unsavedChanges) {
          openUnsavedDialog = true
        } else {
          onBack()
        }
      }, actions = {
        if (uiState.id != 0) {
          IconButton(onClick = { openDeleteDialog = true }) {
            Icon(
              imageVector = Icons.Filled.Delete,
              tint = MaterialTheme.colorScheme.error,
              contentDescription = stringResource(R.string.description_delete_recipe)
            )
          }
        }
        IconButton(onClick = onSubmitChanges, enabled = formIsValid) {
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
      InfoForm(viewModel = viewModel)
      Spacer(modifier = Modifier.padding(8.dp))
      ChapterList(
        chapters = uiState.chapters,
        onEditChapter = { chapter ->
          onEditChapter(chapter ?: ChapterWithStepsAndIngredients(
            value = Chapter(orderNumber = uiState.chapters.size + 1)
          ))
        })
    }
  }
}

@Composable
private fun InfoForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
  val uiState by viewModel.infoUiState.collectAsState()

  FormBase(title = stringResource(R.string.form_title_recipe), modifier = modifier) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FormTextField(
        value = uiState.name,
        onValueChange = { viewModel.setRecipeName(it) },
        label = stringResource(R.string.textfield_recipe_name),
        modifier = Modifier.weight(2f, true)
      )
      FormNumberField(
        value = uiState.servings,
        onValueChange = { viewModel.setServings(it) },
        label = stringResource(R.string.textfield_recipe_servings),
        modifier = Modifier.weight(1f, true)
      )
    }
    Row(
      horizontalArrangement = Arrangement.spacedBy(5.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FormTextField(
        value = uiState.category,
        onValueChange = { viewModel.setCategory(it) },
        label = stringResource(R.string.textfield_recipe_category),
        modifier = Modifier.weight(1f)
      )
      FormTextField(
        value = uiState.subCategory,
        onValueChange = { viewModel.setSubCategory(it) },
        label = stringResource(R.string.textfield_recipe_subcategory),
        imeAction = ImeAction.Done,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun ChapterList(
  chapters: List<ChapterWithStepsAndIngredients>,
  onEditChapter: (ChapterWithStepsAndIngredients?) -> Unit,
  modifier: Modifier = Modifier
) {
  FormList(
    title = stringResource(R.string.form_list_title_chapters),
    onAddClick = { onEditChapter(null) },
    modifier = modifier
  ) {
    for (chapter in chapters) {
      ChapterItem(
        chapter = chapter,
        onClick = { onEditChapter(chapter) },
        modifier = Modifier
          .padding(horizontal = 8.dp, vertical = 12.dp)
          .fillMaxWidth()
      )
      Divider()
    }
  }
}

@Composable
private fun ChapterItem(
  chapter: ChapterWithStepsAndIngredients,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = Modifier
    .clickable {
      onClick()
    }
    .testTag(Tags.CHAPTER_ITEM.name)) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier.fillMaxWidth()) {
      Text(
        text = "${chapter.value.orderNumber}. ${chapter.value.name}",
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
}

private fun formValidation(uiState: EditRecipeViewModel.InfoScreenUiState): Boolean {
  return uiState.name.isNotEmpty()
          && uiState.category.isNotEmpty()
          && uiState.chapters.isNotEmpty()
}

