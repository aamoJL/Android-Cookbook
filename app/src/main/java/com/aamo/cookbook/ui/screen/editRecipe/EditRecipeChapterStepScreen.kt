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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.FormTextField
import com.aamo.cookbook.ui.components.IngredientListItem

class EditRecipeChapterStepScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditIngredient: (index: Int) -> Unit,
    onSubmitChanges: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.stepUiState.collectAsState()
    val formIsValid by remember(uiState) {
      mutableStateOf(formValidation(uiState))
    }

    Column(modifier = modifier.fillMaxSize()) {
      Column(Modifier.weight(1f, true)) {
        StepForm(viewModel = viewModel)
        EditRecipeScreen().ListTitleBar(title = "Ainesosat", onAddClick = {
          onEditIngredient(uiState.ingredients.size)
        })
        StepList(viewModel, onEditIngredient)
      }
      EditRecipeScreen().SaveButton(enabled = formIsValid, onClick = {
        onSubmitChanges()
      })
    }
  }

  @Composable
  fun StepList(
    viewModel: EditRecipeViewModel,
    onEditIngredient: (index: Int) -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.stepUiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier.verticalScroll(scrollState)) {
      for ((index, ingredient) in uiState.ingredients.withIndex()) {
        IngredientItem(
          ingredient = ingredient,
          onClick = {
            onEditIngredient(index)
          },
          modifier = Modifier.padding(10.dp)
        )
        Divider(thickness = 1.dp)
      }
    }
  }

  @Composable
  fun StepForm(
    viewModel: EditRecipeViewModel,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.stepUiState.collectAsState()

    Surface(modifier) {
      Column(
        modifier = Modifier
          .padding(8.dp, 0.dp, 8.dp, 8.dp)
          .fillMaxWidth()
      ) {
        FormTextField(
          value = uiState.description,
          onValueChange = { viewModel.setStepDescription(it) },
          imeAction = ImeAction.Done,
          label = "vaiheen kuvaus"
        )
      }
    }
  }

  @Composable
  fun IngredientItem(
    ingredient: Recipe.Ingredient,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    Box(modifier = Modifier.clickable {
      onClick()
    }) {
      IngredientListItem(ingredient = ingredient, modifier = modifier)
    }
  }

  private fun formValidation(uiState: EditRecipeViewModel.StepScreenUiState): Boolean {
    return uiState.description.isNotEmpty()
  }
}