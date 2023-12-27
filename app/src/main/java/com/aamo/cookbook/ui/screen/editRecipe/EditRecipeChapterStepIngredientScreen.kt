package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.FormTextField

class EditRecipeChapterStepIngredientScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onSubmitChanges: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.ingredientUiState.collectAsState()
    val formIsValid by remember(uiState) { mutableStateOf(formValidation(uiState)) }

    Column(modifier = modifier.fillMaxSize()) {
      Column(Modifier.weight(1f, true)) {
        IngredientForm(viewModel = viewModel)
      }
      EditRecipeScreen().SaveButton(enabled = formIsValid, onClick = {
        onSubmitChanges()
      })
    }
  }

  @Composable
  fun IngredientForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.ingredientUiState.collectAsState()

    Surface(modifier) {
      Column(
        modifier = Modifier
          .padding(8.dp, 0.dp, 8.dp, 8.dp)
          .fillMaxWidth()
      ) {
        Text(text = "Ainesosa", style = MaterialTheme.typography.titleLarge)
        FormTextField(
          value = uiState.name,
          onValueChange = { viewModel.setIngredientName(it) },
          label = "Nimi"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FormTextField(
            value = uiState.amount.toString(),
            onValueChange = { viewModel.setIngredientAmount(it.toFloatOrNull() ?: 0f) },
            label = "Määrä",
            modifier = Modifier.weight(1f, true)
          )
          FormTextField(
            value = uiState.unit,
            onValueChange = { viewModel.setIngredientUnit(it) },
            label = "Mitta",
            imeAction = ImeAction.Done,
            modifier = Modifier.width(100.dp)
          )
        }
      }
    }
  }

  private fun formValidation(uiState: EditRecipeViewModel.IngredientScreenUiState): Boolean {
    return uiState.name.isNotEmpty()
  }
}