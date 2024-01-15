package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.EditRecipeViewModel

@Composable
fun EditRecipeChapterStepScreen(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier,
  onEditIngredient: (Ingredient?) -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onBack: () -> Unit = {}
) {
  val uiState by viewModel.stepUiState.collectAsState()

  EditRecipeChapterStepScreenContent(
    uiState = uiState,
    modifier = modifier,
    onBack = onBack,
    onSubmitChanges = onSubmitChanges,
    onEditIngredient = onEditIngredient,
    onDeleteIngredient = { viewModel.deleteIngredient(it) },
    onFormStateChange = { viewModel.setStepFormState(it) }
  )
}

@Composable
fun EditRecipeChapterStepScreenContent(
  uiState: EditRecipeViewModel.StepScreenUiState,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onEditIngredient: (Ingredient?) -> Unit = {},
  onDeleteIngredient: (Ingredient) -> Boolean = { false },
  onFormStateChange: (EditRecipeViewModel.StepScreenUiState.StepFormState) -> Unit = {}
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
    when (uiState.unsavedChanges) {
      true -> openUnsavedDialog = true
      false -> onBack()
    }
  }

  Scaffold(
    topBar = {
      BasicTopAppBar(when (uiState.id) {
        0 -> stringResource(R.string.screen_title_new_step)
        else -> stringResource(R.string.screen_title_existing_step)
      }, onBack = {
        when (uiState.unsavedChanges) {
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
      StepForm(
        uiState = uiState.formState,
        orderNumber = uiState.orderNumber,
        onStateChange = onFormStateChange
      )
      Spacer(modifier = Modifier.padding(8.dp))
      IngredientList(
        ingredients = uiState.ingredients,
        onEditIngredient = onEditIngredient,
        onDeleteIngredient = onDeleteIngredient
      )
    }
  }
}

@Composable
private fun IngredientList(
  ingredients: List<Ingredient>,
  onEditIngredient: (Ingredient?) -> Unit,
  onDeleteIngredient: (Ingredient) -> Boolean,
  modifier: Modifier = Modifier
) {
  FormList(
    title = stringResource(R.string.form_list_title_ingredients),
    onAddClick = { onEditIngredient(null) },
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = ingredients,
        key = { _, ingredient -> ingredient.hashCode() }
      ) { _, ingredient ->
        IngredientListItem(
          ingredient = ingredient,
          onClick = { onEditIngredient(ingredient) },
          onDismiss = { onDeleteIngredient(ingredient) },
          modifier = Modifier.padding(vertical = 16.dp)
        )
        Divider(thickness = 1.dp)
      }
    }
  }
}

@Composable
private fun StepForm(
  uiState: EditRecipeViewModel.StepScreenUiState.StepFormState,
  orderNumber: Int,
  modifier: Modifier = Modifier,
  onStateChange: (EditRecipeViewModel.StepScreenUiState.StepFormState) -> Unit = {},
) {
  FormBase(title = stringResource(R.string.form_title_step, orderNumber), modifier = modifier) {
    FormTextField(
      value = uiState.description,
      onValueChange = { onStateChange(uiState.copy(description = it)) },
      imeAction = ImeAction.Done,
      label = stringResource(R.string.textfield_step_description)
    )
  }
}

@Composable
private fun IngredientListItem(
  ingredient: Ingredient,
  onClick: () -> Unit,
  onDismiss: () -> (Boolean),
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(Tags.INGREDIENT_ITEM.name),
      headlineContent = {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = modifier.padding(horizontal = 8.dp)
        ) {
          Text(
            text = if (ingredient.amount == 0f) "" else ingredient.amount.toFractionFormattedString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End,
          )
          Text(
            text = ingredient.unit,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
          )
          Text(
            text = ingredient.name,
            style = MaterialTheme.typography.titleMedium,
          )
        }
      })
  }
}