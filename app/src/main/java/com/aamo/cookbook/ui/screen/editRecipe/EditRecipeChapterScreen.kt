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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
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
fun EditRecipeChapterScreen(
  viewModel: EditRecipeViewModel,
  onEditStep: (StepWithIngredients) -> Unit,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.chapterUiState.collectAsState()

  EditRecipeChapterScreenContent(
    uiState = uiState,
    onFormStateChange = { viewModel.setChapterFormState(it) },
    onEditStep = onEditStep,
    onSubmitChanges = onSubmitChanges,
    onBack = onBack,
    modifier = modifier
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeChapterScreenContent(
  uiState: EditRecipeViewModel.ChapterScreenUiState,
  modifier: Modifier = Modifier,
  onFormStateChange: (EditRecipeViewModel.ChapterScreenUiState.ChapterFormState) -> Unit = {},
  onEditStep: (StepWithIngredients) -> Unit = {},
  onSubmitChanges: () -> Unit = {},
  onBack: () -> Unit = {},
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
    if (uiState.unsavedChanges) openUnsavedDialog = true
    else onBack()
  }

  Scaffold(
    topBar = {
      BasicTopAppBar(title = when (uiState.id) {
        0 -> stringResource(R.string.screen_title_new_chapter)
        else -> stringResource(R.string.screen_title_existing_chapter)
      }, onBack = {
        if (uiState.unsavedChanges) openUnsavedDialog = true
        else onBack()
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
      ChapterForm(
        uiState = uiState.formState,
        orderNumber = uiState.orderNumber,
        onFormStateChange = onFormStateChange
      )
      Spacer(modifier = Modifier.padding(8.dp))
      StepList(
        steps = uiState.steps,
        onEditStep = { step ->
          onEditStep(step ?: StepWithIngredients(value = Step(orderNumber = uiState.steps.size + 1)))
        }
      )
    }
  }
}

@Composable
private fun StepList(
  steps: List<StepWithIngredients>,
  onEditStep: (StepWithIngredients?) -> Unit,
  modifier: Modifier = Modifier
) {
  FormList(
    title = stringResource(R.string.form_list_title_steps),
    onAddClick = { onEditStep(null) },
    modifier = modifier
  ) {
    for (step in steps) {
      StepItem(
        step = step,
        onClick = { onEditStep(step) },
        modifier = Modifier.padding(15.dp)
      )
      Divider()
    }
  }
}

@Composable
fun ChapterForm(
  uiState: EditRecipeViewModel.ChapterScreenUiState.ChapterFormState,
  orderNumber: Int,
  onFormStateChange: (EditRecipeViewModel.ChapterScreenUiState.ChapterFormState) -> Unit,
  modifier: Modifier = Modifier
) {
  FormBase(title = stringResource(R.string.form_title_chapter, orderNumber), modifier = modifier) {
    FormTextField(
      value = uiState.name,
      onValueChange = { onFormStateChange(uiState.copy(name = it)) },
      imeAction = ImeAction.Done,
      label = stringResource(R.string.textfield_chapter_name)
    )
  }
}

@Composable
fun StepItem(
  step: StepWithIngredients,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = Modifier.clickable { onClick() }.testTag(Tags.STEP_ITEM.name)) {
    Column(modifier = modifier.fillMaxWidth()) {
      Text(
        text = "${step.value.orderNumber}. ${step.value.getDescriptionWithFormattedEndChar(step.ingredients.isEmpty())}",
        style = MaterialTheme.typography.titleMedium,
      )
      Column(
        modifier = Modifier
          .padding(start = 8.dp)
          .width(IntrinsicSize.Max)
      ) {
        for (ingredient in step.ingredients) {
          Row {
            Text(
              text = if (ingredient.amount == 0f) "" else ingredient.amount.toFractionFormattedString(),
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
                .padding(horizontal = 8.dp)
                .weight(1f)
            )
            Text(
              text = ingredient.name,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.weight(5f)
            )
          }
        }
      }
    }
  }
}
