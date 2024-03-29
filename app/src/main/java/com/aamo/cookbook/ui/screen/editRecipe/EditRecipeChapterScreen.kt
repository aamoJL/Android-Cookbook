package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
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
fun EditRecipeChapterScreen(
  viewModel: EditRecipeViewModel,
  onEditStep: (index: Int) -> Unit,
  onSubmitChanges: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.chapterUiState.collectAsState()

  EditRecipeChapterScreenContent(
    uiState = uiState,
    onFormStateChange = { viewModel.setChapterFormState(it) },
    onDeleteStep = { viewModel.deleteStep(it) },
    onEditStep = onEditStep,
    onSubmitChanges = onSubmitChanges,
    onBack = onBack,
    onSwapSteps = { from, to -> viewModel.swapStepPositions(from, to) },
    modifier = modifier
  )
}

@Composable
fun EditRecipeChapterScreenContent(
  uiState: EditRecipeViewModel.ChapterScreenUiState,
  modifier: Modifier = Modifier,
  onFormStateChange: (EditRecipeViewModel.ChapterScreenUiState.ChapterFormState) -> Unit = {},
  onEditStep: (index: Int) -> Unit = {},
  onDeleteStep: (index: Int) -> (Boolean) = { false },
  onSubmitChanges: () -> Unit = {},
  onBack: () -> Unit = {},
  onSwapSteps: (from: Int, to: Int) -> Unit = {_,_ -> }
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
      BasicTopAppBar(title = when (uiState.isNewChapter) {
        true -> stringResource(R.string.screen_title_new_chapter)
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
        chapterNumber = uiState.index + 1,
        onFormStateChange = onFormStateChange
      )
      Spacer(modifier = Modifier.padding(8.dp))
      StepList(
        steps = uiState.steps,
        onEditStep = onEditStep,
        onDeleteStep = onDeleteStep,
        onSwap = onSwapSteps
      )
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StepList(
  steps: List<Pair<UUID, StepWithIngredients>>,
  onEditStep: (index: Int) -> Unit,
  modifier: Modifier = Modifier,
  onSwap: (from: Int, to: Int) -> Unit,
  onDeleteStep: (index: Int) -> Boolean,
) {
  FormList(
    title = stringResource(R.string.form_list_title_steps),
    onAddClick = { onEditStep(-1) },
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = steps,
        key = { _, pair -> pair.first }
      ) { index, pair ->
        Column(modifier = Modifier.animateItemPlacement()) {
          StepListItem(
            step = pair.second,
            stepNumber = index + 1,
            onClick = { onEditStep(index) },
            onDismiss = { onDeleteStep(index) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            } else null,
            onMoveDown = if (index != steps.size - 1) {
              { onSwap(index, index + 1) }
            } else null
          )
          if (index != steps.size - 1) Divider()
        }
      }
    }
  }
}

@Composable
fun ChapterForm(
  uiState: EditRecipeViewModel.ChapterScreenUiState.ChapterFormState,
  chapterNumber: Int,
  onFormStateChange: (EditRecipeViewModel.ChapterScreenUiState.ChapterFormState) -> Unit,
  modifier: Modifier = Modifier
) {
  FormBase(title = stringResource(R.string.form_title_chapter, chapterNumber), modifier = modifier) {
    FormTextField(
      value = uiState.name,
      onValueChange = { onFormStateChange(uiState.copy(name = it)) },
      label = stringResource(R.string.textfield_chapter_name)
    )
    FormTextField(
      value = uiState.note,
      onValueChange = { onFormStateChange(uiState.copy(note = it))},
      label = stringResource(R.string.textfield_label_note).asOptionalLabel(),
      keyboardOptions = FormTextFieldDefaults.keyboardOptions.copy(
        imeAction = ImeAction.Done
      ),
    )
  }
}

@Composable
fun StepListItem(
  step: StepWithIngredients,
  stepNumber: Int,
  onClick: () -> Unit,
  onDismiss: () -> Boolean,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  BasicDismissibleItem(dismissAction = onDismiss, modifier = modifier) {
    ListItem(
      modifier = modifier
        .clickable { onClick() }
        .testTag(Tags.STEP_ITEM.name),
      headlineContent = {
        Text(
          text = "${stepNumber}. ${step.value.getDescriptionWithFormattedEndChar(step.ingredients.isEmpty())}",
          style = MaterialTheme.typography.titleMedium,
        )
      },
      supportingContent = {
        IngredientList(
          ingredients = step.ingredients,
          modifier = Modifier.padding(start = 16.dp)
        )
      },
      overlineContent = step.value.timerMinutes?.let {
        {
          Text(
            text = stringResource(
              R.string.minutes_amount_abbreviation,
              step.value.timerMinutes.toString()
            ),
            style = MaterialTheme.typography.labelSmall
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

@Composable
private fun IngredientList(
  ingredients: List<Ingredient>,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
      ingredients.forEach {
        Text(
          text = if (it.amount == 0f) "" else it.amount.toFractionFormattedString(),
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
      ingredients.forEach {
        Text(
          text = it.unit,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
        )
      }
    }
    Column {
      ingredients.forEach {
        Text(
          text = it.name,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    EditRecipeChapterScreenContent(
      uiState = EditRecipeViewModel.ChapterScreenUiState(
        formState = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(name = "Name"),
        steps = listOf(
          Pair(
            UUID.randomUUID(), StepWithIngredients(
              value = Step(description = "Description..."),
              ingredients = listOf(
                Ingredient(name = "Ingredient", amount = 250f, unit = "g")
              )
            )
          ),
          Pair(
            UUID.randomUUID(), StepWithIngredients(
              value = Step(description = "Description..."),
              ingredients = listOf(
                Ingredient(name = "Ingredient", amount = 250f, unit = "g")
              )
            )
          )
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
