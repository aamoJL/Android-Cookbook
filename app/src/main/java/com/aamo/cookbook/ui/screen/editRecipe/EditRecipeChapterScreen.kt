package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.FormTextField
import com.aamo.cookbook.ui.components.IngredientListItem

class EditRecipeChapterScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditStep: (index: Int) -> Unit,
    onSubmitChanges: () -> Unit,
    modifier: Modifier = Modifier,
  ) {
    val uiState by viewModel.chapterUiState.collectAsState()
    val formIsValid by remember(uiState) {
      mutableStateOf(formValidation(uiState))
    }

    Column(modifier = modifier.fillMaxSize()) {
      Column(modifier = Modifier.weight(1f, true)) {
        ChapterForm(viewModel = viewModel)
        EditRecipeScreen().ListTitleBar(title = "Vaiheet", onAddClick = {
          onEditStep(uiState.steps.size)
        })
        StepList(viewModel = viewModel, onEditStep = onEditStep)
      }
      EditRecipeScreen().SaveButton(enabled = formIsValid, onClick = {
        onSubmitChanges()
      })
    }
  }

  @Composable
  fun StepList(
    viewModel: EditRecipeViewModel,
    onEditStep: (index: Int) -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.chapterUiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.verticalScroll(scrollState)) {
      for ((index, step) in uiState.steps.withIndex()) {
        StepItem(
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

    Surface(modifier) {
      Column(
        Modifier
          .padding(8.dp)
          .fillMaxWidth()
      ) {
        Text(text = "${uiState.number}. Kappale", style = MaterialTheme.typography.titleLarge)
        FormTextField(
          value = uiState.name,
          onValueChange = { viewModel.setChapterName(it) },
          imeAction = ImeAction.Done,
          label = "Nimi"
        )
      }
    }
  }

  @Composable
  fun StepItem(step: Recipe.Chapter.Step, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.clickable {
      onClick()
    }) {
      Column(modifier = modifier) {
        Text(
          text = step.description,
          style = MaterialTheme.typography.titleLarge,
          textDecoration = TextDecoration.Underline
        )
        Column(modifier = Modifier.padding(4.dp)) {
          for (ingredient in step.ingredients) {
            IngredientListItem(ingredient = ingredient)
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