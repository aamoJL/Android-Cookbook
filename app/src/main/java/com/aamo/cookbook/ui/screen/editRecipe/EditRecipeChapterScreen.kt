package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.toStringWithoutZero

class EditRecipeChapterScreen {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditStep: (index: Int) -> Unit,
    onSubmitChanges: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
  ) {
    val uiState by viewModel.chapterUiState.collectAsState()
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
        })
      },
      bottomBar = {
        SaveButton(
          enabled = formIsValid,
          onClick = {
            onSubmitChanges()
          },
          modifier = Modifier.padding(8.dp)
        )
      }
    ) {
      Column(
        modifier = modifier
          .padding(it)
          .padding(8.dp)
      ) {
        ChapterForm(viewModel = viewModel)
        Spacer(modifier = Modifier.padding(8.dp))
        StepList(viewModel = viewModel, onEditStep = onEditStep)
      }
    }
  }

  @Composable
  fun StepList(
    viewModel: EditRecipeViewModel,
    onEditStep: (index: Int) -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.chapterUiState.collectAsState()

    FormList(
      title = "Vaiheet",
      onAddClick = { onEditStep(uiState.steps.size) },
      modifier = modifier
    ) {
      for ((index, step) in uiState.steps.withIndex()) {
        StepItem(
          number = index + 1,
          step = step,
          onClick = { onEditStep(index) },
          modifier = Modifier.padding(15.dp)
        )
        Divider()
      }
    }
  }

  @Composable
  fun ChapterForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.chapterUiState.collectAsState()

    FormBase(title = "${uiState.number}. Kappaleen tiedot", modifier = modifier) {
      FormTextField(
        value = uiState.name,
        onValueChange = { viewModel.setChapterName(it) },
        imeAction = ImeAction.Done,
        label = "Nimi"
      )
    }
  }

  @Composable
  fun StepItem(
    number: Int,
    step: Recipe.Chapter.Step,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    Box(modifier = Modifier.clickable {
      onClick()
    }) {
      Column(modifier = modifier.fillMaxWidth()) {
        Text(
          text = "${number}. ${step.getDescriptionWithFormattedEndChar()}",
          style = MaterialTheme.typography.titleMedium,
        )
        Column(
          modifier = Modifier
            .padding(start = 8.dp)
            .width(IntrinsicSize.Min)
        ) {
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
                  .padding(start = 8.dp)
                  .weight(1f)
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

  private fun formValidation(uiState: EditRecipeViewModel.ChapterScreenUiState): Boolean {
    return uiState.name.isNotEmpty()
            && uiState.steps.isNotEmpty()
  }
}