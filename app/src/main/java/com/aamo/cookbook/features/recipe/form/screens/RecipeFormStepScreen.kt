package com.aamo.cookbook.features.recipe.form.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.inputs.IntNumberField
import com.aamo.cookbook.ui.components.inputs.LoadingIconButton
import com.aamo.cookbook.ui.components.inputs.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.tags.UITag
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormStepScreen(val index: Int)

class RecipeFormStepScreenViewModel(
  formData: RecipeFormStepFields,
  private val saveData: suspend (RecipeFormStepFields) -> Unit,
) : ViewModel() {
  class FormState(formData: RecipeFormStepFields) {
    val description = ViewModelState(formData.description).onChange { onUnsavedChanges() }
    val timerMinutes = ViewModelState(formData.timerMinutes ?: 1).onChange { onUnsavedChanges() }
    val note = ViewModelState(formData.note).onChange { onUnsavedChanges() }
    val ingredients = ViewModelStateList(formData.ingredients).onChange { onUnsavedChanges() }
    var savingState by mutableStateOf(SavingState())

    fun canSave(): Boolean {
      if (savingState.state == SavingState.State.SAVING) return false
      if (description.value.isEmpty()) return false
      return true
    }

    private fun onUnsavedChanges() {
      if (!savingState.unsavedChanges) {
        savingState = savingState.copy(unsavedChanges = true)
      }
    }
  }

  val formState = FormState(formData)
  val isNew = formData.description.isEmpty()

  fun save() {
    if (!formState.canSave()) return

    formState.apply { savingState = savingState.getAsSaving() }

    viewModelScope.launch {
      saveData(formState.let {
        RecipeFormStepFields(
          description = it.description.value,
          timerMinutes = it.timerMinutes.value,
          note = it.note.value,
          ingredients = it.ingredients.values
        )
      })
    }.invokeOnCompletion {
      formState.apply { savingState = savingState.getAsSaved() }
    }
  }
}

fun NavGraphBuilder.recipeFormStepScreen(
  formData: (index: Int) -> RecipeFormStepFields,
  onSubmit: (RecipeFormStepFields) -> Unit,
  onBack: () -> Unit,
) {
  composable<RecipeFormStepScreen> { navStack ->
    val (index) = navStack.toRoute<RecipeFormStepScreen>()
    val viewmodel: RecipeFormStepScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormStepScreenViewModel(
          formData = formData(index),
          saveData = onSubmit,
        )
      }
    })

    val stepNavController = rememberNavController()

    NavHost(
      navController = stepNavController, startDestination = RecipeFormStepScreen(index = index)
    ) {
      composable<RecipeFormStepScreen> {
        RecipeFormStepScreenContent(
          formState = viewmodel.formState,
          isNew = viewmodel.isNew,
          stepIndex = index,
          onNewIngredient = { stepNavController.navigate(RecipeFormIngredientScreen(index = 0)) },
          onEditIngredient = { TODO() },
          onDeleteIngredient = { TODO() },
          onSwapIngredients = { _, _ -> TODO() },
          onSubmit = { viewmodel.save() },
          onBack = onBack,
        )
      }
      recipeFormIngredientScreen(formData = { index ->
        viewmodel.formState.ingredients.values.elementAtOrElse(index) { RecipeFormIngredientFields() }
      }, onSubmit = { TODO() }, onBack = {
        stepNavController.navigateUp()
      })
    }
  }
}

@Composable
fun RecipeFormStepScreenContent(
  formState: RecipeFormStepScreenViewModel.FormState,
  isNew: Boolean,
  stepIndex: Int,
  onNewIngredient: () -> Unit,
  onEditIngredient: (index: Int) -> Unit,
  onDeleteIngredient: (RecipeFormIngredientFields) -> (Boolean),
  onSwapIngredients: (from: Int, to: Int) -> Unit,
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
        true -> stringResource(R.string.screen_title_new_step)
        else -> stringResource(R.string.screen_title_existing_step)
      },
      onBack = { if (formState.savingState.unsavedChanges) openUnsavedDialog = true else onBack() },
      actions = {
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
      StepForm(formState = formState, orderNumber = stepIndex + 1)
      Spacer(modifier = Modifier.padding(8.dp))
      StepFormIngredientList(
        ingredients = formState.ingredients.values,
        onNewIngredient = onNewIngredient,
        onEditIngredient = onEditIngredient,
        onDeleteIngredient = onDeleteIngredient,
        onSwap = onSwapIngredients,
      )
    }
  }
}

@Composable
private fun StepForm(
  formState: RecipeFormStepScreenViewModel.FormState,
  orderNumber: Int,
) {
  FormBase(title = stringResource(R.string.form_title_step, orderNumber)) {
    TextField(
      value = formState.description.value,
      onValueChange = { formState.description.update(it) },
      label = { Text(stringResource(R.string.label_description)) },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
      ),
      modifier = Modifier.fillMaxWidth()
    )
    IntNumberField(
      value = formState.timerMinutes.value,
      label = { Text(stringResource(R.string.label_step_timer)) },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      onValueChange = { formState.timerMinutes.update(it) },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
private fun StepFormIngredientList(
  ingredients: List<RecipeFormIngredientFields>,
  onNewIngredient: () -> Unit,
  onEditIngredient: (index: Int) -> Unit,
  onDeleteIngredient: (RecipeFormIngredientFields) -> Boolean,
  onSwap: (from: Int, to: Int) -> Unit,
) {
  FormList(
    title = stringResource(R.string.form_list_title_ingredients),
    onAddClick = onNewIngredient,
  ) {
    LazyColumn {
      itemsIndexed(
        items = ingredients,
        key = { _, ingredient -> ingredient.uuid },
      ) { index, ingredient ->
        Column {
          IngredientListItem(
            ingredient = ingredient,
            onClick = { onEditIngredient(index) },
            onDismiss = { onDeleteIngredient(ingredient) },
            onMoveUp = if (index != 0) {
              { onSwap(index, index - 1) }
            }
            else null,
            onMoveDown = if (index != ingredients.size - 1) {
              { onSwap(index, index + 1) }
            }
            else null,
            modifier = Modifier.padding(vertical = 16.dp))

          if (index != ingredients.size - 1) {
            HorizontalDivider()
          }
        }
      }
    }
  }
}

@Composable
private fun IngredientListItem(
  ingredient: RecipeFormIngredientFields,
  onClick: () -> Unit,
  onDismiss: () -> Unit,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss) {
    ListItem(
      modifier = Modifier
        .clickable { onClick() }
        .testTag(UITag.INGREDIENT_ITEM.name),
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