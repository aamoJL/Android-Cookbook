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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
  viewModel: EditRecipeViewModel,
  onEditChapter: (index: Int) -> Unit,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.infoUiState.collectAsState()
  val formIsValid by remember(uiState) {
    mutableStateOf(formValidation(uiState))
  }
  var openUnsavedDialog by remember { mutableStateOf(false) }

  if (openUnsavedDialog) {
    UnsavedDialog(
      onDismiss = {
        openUnsavedDialog = false
      },
      onConfirm = {
        openUnsavedDialog = false
        onBack()
      })
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
      BasicTopAppBar(title = uiState.screenTitle, onBack = {
        if (uiState.unsavedChanges) {
          openUnsavedDialog = true
        } else {
          onBack()
        }
      }, actions = {
        IconButton(onClick = onSubmitChanges) {
          Icon(imageVector = Icons.Filled.Done, contentDescription = "Tallenna resepti")
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
      ChapterList(viewModel = viewModel, onEditChapter = onEditChapter)
    }
  }
}

@Composable
private fun InfoForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
  val uiState by viewModel.infoUiState.collectAsState()

  FormBase(title = "Reseptin tiedot", modifier = modifier) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      FormTextField(
        value = uiState.name,
        onValueChange = { viewModel.setRecipeName(it) },
        label = "Nimi",
        modifier = Modifier.weight(2f, true)
      )
      FormNumberField(
        value = uiState.servings,
        onValueChange = { viewModel.setServings(it) },
        label = "Annokset",
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
        label = "Kategoria",
        modifier = Modifier.weight(1f)
      )
      FormTextField(
        value = uiState.subCategory,
        onValueChange = { viewModel.setSubCategory(it) },
        label = "(Alakategoria)",
        imeAction = ImeAction.Done,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun ChapterList(
  viewModel: EditRecipeViewModel,
  onEditChapter: (index: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.infoUiState.collectAsState()

  FormList(
    title = "Kappaleet",
    onAddClick = {
      onEditChapter(-1)
    },
    modifier = modifier
  ) {
    for ((index, chapter) in uiState.chapters.withIndex()) {
      ChapterItem(
        number = index + 1,
        chapter = chapter,
        onClick = { onEditChapter(index) },
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
  number: Int,
  chapter: Recipe.Chapter,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = Modifier.clickable {
    onClick()
  }) {
    Column(modifier = modifier.fillMaxWidth()) {
      Text(text = "${number}. ${chapter.name}", style = MaterialTheme.typography.titleMedium)
      Column(
        modifier = Modifier
          .padding(start = 16.dp)
          .width(IntrinsicSize.Min)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          for ((index, step) in chapter.steps.withIndex()) {
            Column {
              Text(
                text = "${index + 1}. ${step.getDescriptionWithFormattedEndChar()}",
                style = MaterialTheme.typography.bodyMedium
              )
              Column(modifier = Modifier.padding(start = 16.dp)) {
                for (ingredient in step.ingredients) {
                  Row {
                    Text(
                      text = ingredient.amount.toStringWithoutZero(),
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
                        .padding(start = 4.dp)
                    )
                    Text(
                      text = ingredient.name,
                      style = MaterialTheme.typography.bodySmall,
                      modifier = Modifier.weight(2f)
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

