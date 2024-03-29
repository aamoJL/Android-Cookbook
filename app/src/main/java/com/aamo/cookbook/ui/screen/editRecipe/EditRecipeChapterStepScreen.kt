package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.FormTextFieldDefaults
import com.aamo.cookbook.ui.components.form.SaveButton
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.asOptionalLabel
import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import java.util.UUID

@Composable
fun EditRecipeChapterStepScreen(
  viewModel: EditRecipeViewModel,
  modifier: Modifier = Modifier,
  onEditIngredient: (index: Int) -> Unit = {},
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
    onFormStateChange = { viewModel.setStepFormState(it) },
    onSwapIngredients = { from, to -> viewModel.swapIngredientPositions(from, to) }
  )
}

@Composable
fun EditRecipeChapterStepScreenContent(
  uiState: EditRecipeViewModel.StepScreenUiState,
  modifier: Modifier = Modifier,
  onBack: () -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onEditIngredient: (index: Int) -> Unit = {},
  onDeleteIngredient: (index: Int) -> Boolean = { false },
  onFormStateChange: (EditRecipeViewModel.StepScreenUiState.StepFormState) -> Unit = {},
  onSwapIngredients: (from: Int, to: Int) -> Unit = {_,_ -> }
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
      BasicTopAppBar(when (uiState.isNewStep) {
        true -> stringResource(R.string.screen_title_new_step)
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
        orderNumber = uiState.index + 1,
        onStateChange = onFormStateChange
      )
      Spacer(modifier = Modifier.padding(8.dp))
      IngredientList(
        ingredients = uiState.ingredients,
        onEditIngredient = onEditIngredient,
        onDeleteIngredient = onDeleteIngredient,
        onSwap = onSwapIngredients
      )
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
      label = stringResource(R.string.textfield_step_description)
    )
    FormNumberField(
      value = uiState.timerMinutes,
      onValueChange = { onStateChange(uiState.copy(timerMinutes = it)) },
      label = stringResource(R.string.textfield_step_timer).asOptionalLabel()
    )
    FormTextField(
      value = uiState.note,
      onValueChange = { onStateChange(uiState.copy(note = it))},
      label = stringResource(R.string.textfield_label_note).asOptionalLabel(),
      keyboardOptions = FormTextFieldDefaults.keyboardOptions.copy(
        imeAction = ImeAction.Done
      ),
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IngredientList(
  ingredients: List<Pair<UUID, Ingredient>>,
  onEditIngredient: (index: Int) -> Unit,
  onDeleteIngredient: (index: Int) -> Boolean,
  onSwap: (from: Int, to: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  FormList(
    title = stringResource(R.string.form_list_title_ingredients),
    onAddClick = { onEditIngredient(-1) },
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = ingredients,
        key = { _, pair -> pair.first },
      ) { index, pair ->
        Column(modifier = Modifier.animateItemPlacement()) {
          IngredientListItem(
            ingredient = pair.second,
            onClick = { onEditIngredient(index) },
            onDismiss = { onDeleteIngredient(index) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            } else null,
            onMoveDown = if (index != ingredients.size - 1) {
              { onSwap(index, index + 1) }
            } else null,
            modifier = Modifier.padding(vertical = 16.dp)
          )
          if(index != ingredients.size - 1) Divider()
        }
      }
    }
  }
}

@Composable
private fun IngredientListItem(
  ingredient: Ingredient,
  onClick: () -> Unit,
  onDismiss: () -> (Boolean),
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
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
      },
      trailingContent = {
        Column(modifier = Modifier) {
          if (onMoveUp != null) IconButton(onClick = onMoveUp) {
            Icon(
              imageVector = Icons.Filled.KeyboardArrowUp,
              contentDescription = stringResource(R.string.description_move_up)
            )
          }
          if (onMoveDown != null) IconButton(onClick = onMoveDown) {
            Icon(
              imageVector = Icons.Filled.KeyboardArrowDown,
              contentDescription = stringResource(R.string.description_move_down)
            )
          }
        }
      }
    )
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    EditRecipeChapterStepScreenContent(
      uiState = EditRecipeViewModel.StepScreenUiState(
        formState = EditRecipeViewModel.StepScreenUiState.StepFormState(description = "Description"),
        ingredients = listOf(
          Pair(UUID.randomUUID(), Ingredient(name = "Ingredient", amount = 250f, unit = "g")),
          Pair(UUID.randomUUID(), Ingredient(name = "Ingredient", amount = 250f, unit = "g"))
        )
      )
    )
  }
}

@PreviewLightDark
@Composable
private fun PreviewUnsavedDialog() {
  CookbookTheme {
    UnsavedDialog(onDismiss = {}) {}
  }
}