package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormFloatField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.FormTextFieldDefaults
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@Composable
fun EditRecipeChapterStepIngredientScreen(
  viewModel: EditRecipeViewModel,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.ingredientUiState.collectAsState()

  EditRecipeChapterStepIngredientScreenContent(
    uiState = uiState,
    modifier = modifier,
    onBack = onBack,
    onSubmitChanges = onSubmitChanges,
    onFormStateChange = { viewModel.setIngredientFormState(it) }
  )
}

@Composable
fun EditRecipeChapterStepIngredientScreenContent(
  uiState: EditRecipeViewModel.IngredientScreenUiState,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onFormStateChange: (EditRecipeViewModel.IngredientScreenUiState.IngredientFormState) -> Unit = {},
) {
  var openUnsavedDialog by remember { mutableStateOf(false) }

  if (openUnsavedDialog) {
    UnsavedDialog(
      onDismiss = { openUnsavedDialog = false },
      onConfirm = {
        openUnsavedDialog = false
        onBack()
      })
  }

  BackHandler(true) {
    when(uiState.unsavedChanges){
      true -> openUnsavedDialog = true
      false -> onBack()
    }
  }

  Scaffold(
    topBar = {
      BasicTopAppBar(title = when (uiState.isNewIngredient) {
        true -> stringResource(R.string.screen_title_new_ingredient)
        else -> stringResource(R.string.screen_title_existing_ingredient)
      }, onBack = {
        when(uiState.unsavedChanges){
          true -> openUnsavedDialog = true
          false -> onBack()
        }
      })
    },
    bottomBar = {
      SaveButton(
        enabled = uiState.canBeSaved,
        onClick = { onSubmitChanges() },
        modifier = Modifier.padding(8.dp)
      )
    }
  ) {
    Column(
      modifier = modifier
        .padding(it)
        .padding(8.dp)
    ) {
      IngredientForm(
        formState = uiState.formState,
        onStateChange = onFormStateChange
      )
    }
  }
}

@Composable
private fun IngredientForm(
  formState: EditRecipeViewModel.IngredientScreenUiState.IngredientFormState,
  modifier: Modifier = Modifier,
  onStateChange: (EditRecipeViewModel.IngredientScreenUiState.IngredientFormState) -> Unit = {}
) {
  FormBase(title = stringResource(R.string.form_title_ingredient), modifier = modifier) {
    FormTextField(
      value = formState.name,
      onValueChange = { onStateChange(formState.copy(name = it)) },
      label = stringResource(R.string.textfield_ingredient_name)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      FormFloatField(
        value = if (formState.amount == 0f) null else formState.amount,
        onValueChange = { onStateChange(formState.copy(amount = it)) },
        label = stringResource(R.string.textfield_ingredient_amount),
        modifier = Modifier.weight(1f, true)
      )
      FormTextField(
        value = formState.unit,
        onValueChange = { onStateChange(formState.copy(unit = it)) },
        label = stringResource(R.string.textfield_ingredient_unit),
        keyboardOptions = FormTextFieldDefaults.keyboardOptions.copy(
          capitalization =  KeyboardCapitalization.None,
          imeAction = ImeAction.Done
        ),
        modifier = Modifier.width(100.dp)
      )
    }
  }
}