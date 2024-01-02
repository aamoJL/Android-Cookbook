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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeChapterStepScreen(
  viewModel: EditRecipeViewModel,
  onEditIngredient: (index: Int) -> Unit,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.stepUiState.collectAsState()
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
      BasicTopAppBar(when (uiState.id) {
        UUID(0, 0) -> stringResource(R.string.screen_title_new_step)
        else -> stringResource(R.string.screen_title_existing_step)
      }, onBack = {
        if (uiState.unsavedChanges) {
          openUnsavedDialog = true
        } else {
          onBack()
        }
      })
    },
    bottomBar = {
      SaveButton(
        enabled = formIsValid, onClick = {
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
      StepForm(viewModel = viewModel)
      Spacer(modifier = Modifier.padding(8.dp))
      StepList(viewModel = viewModel, onEditIngredient = onEditIngredient)
    }
  }
}

@Composable
private fun StepList(
  viewModel: EditRecipeViewModel,
  onEditIngredient: (index: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.stepUiState.collectAsState()

  FormList(
    title = stringResource(R.string.form_list_title_ingredients),
    onAddClick = {
      onEditIngredient(-1)
    },
    modifier = modifier
  ) {
    for ((index, ingredient) in uiState.ingredients.withIndex()) {
      IngredientItem(
        ingredient = ingredient,
        onClick = {
          onEditIngredient(index)
        },
        modifier = Modifier.padding(vertical = 16.dp)
      )
      Divider(thickness = 1.dp)
    }
  }
}

@Composable
private fun StepForm(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.stepUiState.collectAsState()

  FormBase(title = stringResource(R.string.form_title_step), modifier = modifier) {
    FormTextField(
      value = uiState.description,
      onValueChange = { viewModel.setStepDescription(it) },
      imeAction = ImeAction.Done,
      label = stringResource(R.string.textfield_step_description)
    )
  }
}

@Composable
private fun IngredientItem(
  ingredient: Recipe.Ingredient,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = Modifier
    .clickable {
      onClick()
    }
    .fillMaxWidth()) {
    Row(modifier = modifier.width(IntrinsicSize.Min)) {
      Text(
        text = if (ingredient.amount != 0f) ingredient.amount.toStringWithoutZero() else "",
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.End,
        modifier = Modifier
          .defaultMinSize(minWidth = 40.dp)
          .weight(1f)
      )
      Text(
        text = ingredient.unit,
        style = MaterialTheme.typography.titleMedium,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
          .padding(start = 8.dp)
          .weight(1f)
      )
      Text(
        text = ingredient.name,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.weight(2f)
      )
    }
  }
}

private fun formValidation(uiState: EditRecipeViewModel.StepScreenUiState): Boolean {
  return uiState.description.isNotEmpty()
}