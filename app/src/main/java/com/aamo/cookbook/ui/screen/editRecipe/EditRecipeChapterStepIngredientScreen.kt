package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormFloatField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeChapterStepIngredientScreen(
  viewModel: EditRecipeViewModel,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.ingredientUiState.collectAsState()
  val formIsValid by remember(uiState) { mutableStateOf(formValidation(uiState)) }
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
      BasicTopAppBar(title = when (uiState.id) {
        0 -> stringResource(R.string.screen_title_new_ingredient)
        else -> stringResource(R.string.screen_title_existing_ingredient)
      }, onBack = {
        if (uiState.unsavedChanges) {
          openUnsavedDialog = true
        } else {
          onBack()
        }
      })
    },
    bottomBar = {
      SaveButton(enabled = formIsValid, onClick = {
        onSubmitChanges()
      }, modifier = Modifier.padding(8.dp))
    }
  ) {
    Column(
      modifier = modifier
        .padding(it)
        .padding(8.dp)
    ) {
      IngredientForm(viewModel = viewModel)
    }
  }
}

@Composable
private fun IngredientForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
  val uiState by viewModel.ingredientUiState.collectAsState()

  FormBase(title = stringResource(R.string.form_title_ingredient), modifier = modifier) {
    FormTextField(
      value = uiState.name,
      onValueChange = { viewModel.setIngredientName(it) },
      label = stringResource(R.string.textfield_ingredient_name)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      FormFloatField(
        initialValue = if (uiState.amount == 0f) null else uiState.amount,
        onValueChange = { viewModel.setIngredientAmount(it) },
        label = stringResource(R.string.textfield_ingredient_amount),
        modifier = Modifier.weight(1f, true)
      )
      FormTextField(
        value = uiState.unit,
        onValueChange = { viewModel.setIngredientUnit(it) },
        label = stringResource(R.string.textfield_ingredient_unit),
        imeAction = ImeAction.Done,
        modifier = Modifier.width(100.dp)
      )
    }
  }
}

private fun formValidation(uiState: EditRecipeViewModel.IngredientScreenUiState): Boolean {
  return uiState.name.isNotEmpty()
}
