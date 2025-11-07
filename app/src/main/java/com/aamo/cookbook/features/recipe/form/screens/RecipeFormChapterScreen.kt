package com.aamo.cookbook.features.recipe.form.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.components.FormBase
import com.aamo.cookbook.features.recipe.form.components.FormList
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.utility.components.PrimaryTopAppBar
import com.aamo.cookbook.utility.components.inputs.BasicDismissibleItem
import com.aamo.cookbook.utility.components.inputs.LoadingIconButton
import com.aamo.cookbook.utility.components.inputs.borderlessTextFieldColors
import com.aamo.cookbook.utility.components.modals.UnsavedDialog
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.onNotNull
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.tags.UITag
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormChapterScreen(val index: Int)

class RecipeFormChapterScreenViewModel(
  private val formData: RecipeFormChapterFields
) : ViewModel() {
  class FormState(formData: RecipeFormChapterFields) {
    val name = ViewModelState(formData.name).onChange { onUnsavedChanges() }
    val note = ViewModelState(formData.note).onChange { onUnsavedChanges() }
    val steps = ViewModelStateList(formData.steps).onChange { onUnsavedChanges() }
    var savingState by mutableStateOf(SavingState())

    fun canSave(): Boolean {
      if (savingState.state == SavingState.State.SAVING) return false
      if (name.value.isEmpty()) return false
      if (steps.values.isEmpty()) return false
      return true
    }

    private fun onUnsavedChanges() {
      if (!savingState.unsavedChanges) {
        savingState = savingState.copy(unsavedChanges = true)
      }
    }
  }

  val formState = FormState(formData)
  val isNew = formData.name.isEmpty()

  fun update(step: RecipeFormStepFields) {
    formState.steps.values.indexOfFirst { it.uuid == step.uuid }.also { index ->
      if (index == -1) formState.steps.add(step)
      else formState.steps.replaceAt(index, step)
    }
  }

  fun getModel(): RecipeFormChapterFields? {
    if (!formState.canSave()) return null

    formState.apply { savingState = savingState.getAsSaving() }

    return formState.let {
      formData.copy(name = it.name.value, note = it.note.value, steps = it.steps.values)
    }.also {
      formState.apply { savingState = savingState.getAsSaved() }
    }
  }
}

fun NavGraphBuilder.recipeFormChapterScreen(
  formData: (index: Int) -> RecipeFormChapterFields,
  onSubmit: (RecipeFormChapterFields) -> Unit,
  onBack: () -> Unit,
) {
  composable<RecipeFormChapterScreen> { navStack ->
    val (index) = navStack.toRoute<RecipeFormChapterScreen>()
    val viewmodel: RecipeFormChapterScreenViewModel = viewModel(factory = viewModelFactory {
      initializer { RecipeFormChapterScreenViewModel(formData = formData(index)) }
    })

    val chapterNavController = rememberNavController()

    NavHost(
      navController = chapterNavController,
      startDestination = RecipeFormChapterScreen(index = index)
    ) {
      composable<RecipeFormChapterScreen> {
        RecipeFormChapterScreenContent(
          formState = viewmodel.formState,
          isNew = viewmodel.isNew,
          chapterIndex = index,
          onNewStep = {
            chapterNavController.navigate(RecipeFormStepScreen(index = viewmodel.formState.steps.values.size))
          },
          onEditStep = { chapterNavController.navigate(RecipeFormStepScreen(index = it)) },
          onDeleteStep = { viewmodel.formState.steps.remove(it) },
          onSwapSteps = { a, b -> viewmodel.formState.steps.swapAt(a, b) },
          onSubmit = { viewmodel.getModel().onNotNull { onSubmit(it) } },
          onBack = onBack,
        )
      }
      recipeFormStepScreen(formData = { index ->
        viewmodel.formState.steps.values.elementAtOrElse(index) { RecipeFormStepFields() }
      }, onSubmit = { step ->
        viewmodel.update(step)
        chapterNavController.navigateUp()
      }, onBack = {
        chapterNavController.navigateUp()
      })
    }
  }
}

@Composable
fun RecipeFormChapterScreenContent(
  formState: RecipeFormChapterScreenViewModel.FormState,
  isNew: Boolean,
  chapterIndex: Int,
  onNewStep: () -> Unit,
  onEditStep: (index: Int) -> Unit,
  onDeleteStep: (RecipeFormStepFields) -> Unit,
  onSwapSteps: (from: Int, to: Int) -> Unit,
  onSubmit: () -> Unit,
  onBack: () -> Unit,
) {
  var openUnsavedDialog by remember { mutableStateOf(false) }

  UnsavedDialog(open = openUnsavedDialog, onDismiss = { openUnsavedDialog = false }, onConfirm = {
    openUnsavedDialog = false
    onBack()
  })

  BackHandler(enabled = formState.savingState.unsavedChanges) {
    openUnsavedDialog = true
  }

  Scaffold(topBar = {
    PrimaryTopAppBar(
      title = when (isNew) {
      true -> stringResource(R.string.screen_title_new_chapter)
      else -> stringResource(R.string.screen_title_existing_chapter)
    }, onBack = {
      if (formState.savingState.unsavedChanges) openUnsavedDialog = true
      else onBack()
    }, actions = {
      LoadingIconButton(
        onClick = onSubmit,
        isLoading = formState.savingState.state == SavingState.State.SAVING,
        enabled = formState.canSave(),
      ) {
        Icon(
          painter = painterResource(R.drawable.rounded_check_24),
          contentDescription = stringResource(R.string.cd_save)
        )
      }
    })
  }) {
    Column(
      modifier = Modifier
        .padding(it)
        .padding(8.dp)
    ) {
      ChapterForm(formState = formState, chapterNumber = chapterIndex + 1)
      Spacer(modifier = Modifier.padding(8.dp))
      StepList(
        steps = formState.steps.values,
        onNewStep = onNewStep,
        onEditStep = onEditStep,
        onDeleteStep = onDeleteStep,
        onSwap = onSwapSteps
      )
    }
  }
}

@Composable
fun ChapterForm(formState: RecipeFormChapterScreenViewModel.FormState, chapterNumber: Int) {
  FormBase(title = stringResource(R.string.form_title_chapter, chapterNumber)) {
    TextField(
      value = formState.name.value,
      onValueChange = { formState.name.update(it) },
      label = { Text(stringResource(R.string.label_name)) },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
      ),
      modifier = Modifier.fillMaxWidth()
    )
    TextField(
      value = formState.note.value,
      onValueChange = { formState.note.update(it) },
      label = { Text(stringResource(R.string.label_note).asOptionalLabel()) },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
      ),
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StepList(
  steps: List<RecipeFormStepFields>,
  onNewStep: () -> Unit,
  onEditStep: (index: Int) -> Unit,
  modifier: Modifier = Modifier,
  onSwap: (from: Int, to: Int) -> Unit,
  onDeleteStep: (RecipeFormStepFields) -> Unit,
) {
  FormList(
    title = stringResource(R.string.form_list_title_steps),
    onAddClick = onNewStep,
    modifier = modifier
  ) {
    LazyColumn {
      itemsIndexed(
        items = steps, key = { _, step -> step.uuid }) { index, step ->
        Column(modifier = Modifier.animateItem()) {
          StepListItem(
            step = step,
            stepNumber = index + 1,
            onClick = { onEditStep(index) },
            onDismiss = { onDeleteStep(step) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            }
            else null,
            onMoveDown = if (index != steps.size - 1) {
              { onSwap(index, index + 1) }
            }
            else null,
            modifier = Modifier.fillMaxWidth())

          if (index != steps.size - 1) {
            HorizontalDivider()
          }
        }
      }
    }
  }
}

@Composable
fun StepListItem(
  step: RecipeFormStepFields,
  stepNumber: Int,
  onClick: () -> Unit,
  onDismiss: () -> Unit,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss, modifier = modifier) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(UITag.STEP_ITEM.name),
      headlineContent = {
        Text(
          text = "${stepNumber}. ${step.description}${if (step.ingredients.isEmpty()) "." else ":"}",
          style = MaterialTheme.typography.titleMedium,
        )
      },
      supportingContent = {
        StepListIngredientList(ingredients = step.ingredients, modifier = Modifier.padding(16.dp))
      },
      overlineContent = step.timerMinutes?.let {
        {
          Text(
            text = stringResource(R.string.abbreviation_minutes, step.timerMinutes.toString()),
            style = MaterialTheme.typography.labelSmall
          )
        }
      },
      trailingContent = {
        Column(modifier = Modifier) {
          if (onMoveUp != null) IconButton(onClick = onMoveUp) {
            Icon(
              painter = painterResource(R.drawable.rounded_keyboard_arrow_up_24),
              contentDescription = stringResource(R.string.cd_move_up)
            )
          }
          if (onMoveDown != null) IconButton(onClick = onMoveDown) {
            Icon(
              painter = painterResource(R.drawable.rounded_keyboard_arrow_down_24),
              contentDescription = stringResource(R.string.cd_move_down)
            )
          }
        }
      })
  }
}

@Composable
private fun StepListIngredientList(
  ingredients: List<RecipeFormIngredientFields>, modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
      ingredients.forEach {
        Text(
          text = if (it.amount == 0f || it.amount == null) "" else it.amount.toFractionFormattedString(),
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
      ingredients.forEach {
        Text(text = it.unit, style = MaterialTheme.typography.bodySmall, modifier = Modifier)
      }
    }
    Column {
      ingredients.forEach {
        Text(text = it.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier)
      }
    }
  }
}