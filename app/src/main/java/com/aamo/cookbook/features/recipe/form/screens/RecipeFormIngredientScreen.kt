package com.aamo.cookbook.features.recipe.form.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.components.FormBase
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.inputs.LoadingIconButton
import com.aamo.cookbook.ui.components.inputs.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.inputs.number_field.NullableFloatFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.onNotNull
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormIngredientScreen(val index: Int)

class RecipeFormIngredientScreenViewModel(
  private val formData: RecipeFormIngredientFields,
) : ViewModel() {
  // TODO: unit test
  class FormState(formData: RecipeFormIngredientFields) {
    val name = ViewModelState(formData.name).onChange { onUnsavedChanges() }
    val amount = ViewModelState(formData.amount).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange { onUnsavedChanges() }
    val unit = ViewModelState(formData.unit).onChange { onUnsavedChanges() }
    var savingState by mutableStateOf(SavingState())

    // TODO: unit test
    fun canSave(): Boolean {
      if (savingState.state == SavingState.State.SAVING) return false
      if (name.value.isEmpty()) return false
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

  // TODO: unit test
  fun getModel(): RecipeFormIngredientFields? {
    if (!formState.canSave()) return null

    formState.apply { savingState = savingState.getAsSaving() }

    return formState.let {
      formData.copy(
        name = it.name.value, amount = it.amount.value, unit = it.unit.value
      )
    }.also {
      formState.apply { savingState = savingState.getAsSaved() }
    }
  }
}

fun NavGraphBuilder.recipeFormIngredientScreen(
  formData: (index: Int) -> RecipeFormIngredientFields,
  onSubmit: (RecipeFormIngredientFields) -> Unit,
  onBack: () -> Unit,
) {
  composable<RecipeFormIngredientScreen> { navStack ->
    val (index) = navStack.toRoute<RecipeFormIngredientScreen>()
    val viewmodel: RecipeFormIngredientScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormIngredientScreenViewModel(formData = formData(index))
      }
    })

    RecipeFormIngredientScreenContent(
      formState = viewmodel.formState,
      isNew = viewmodel.isNew,
      onBack = onBack,
      onSubmit = { viewmodel.getModel().onNotNull { onSubmit(it) } })
  }
}

@Composable
fun RecipeFormIngredientScreenContent(
  formState: RecipeFormIngredientScreenViewModel.FormState,
  isNew: Boolean,
  onBack: () -> Unit,
  onSubmit: () -> Unit,
) {
  var openUnsavedDialog by rememberSaveable { mutableStateOf(false) }

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
        true -> stringResource(R.string.screen_title_new_ingredient)
        else -> stringResource(R.string.screen_title_existing_ingredient)
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
      IngredientForm(formState = formState)
    }
  }
}

@Composable
private fun IngredientForm(
  formState: RecipeFormIngredientScreenViewModel.FormState,
) {
  FormBase(title = stringResource(R.string.title_ingredient)) {
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
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
      NumberField(
        value = formState.amount.value,
        onValueChange = { formState.amount.update(it) },
        validator = NullableFloatFieldValidator,
        label = { Text(stringResource(R.string.label_amount).asOptionalLabel()) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.weight(1f, true)
      )
      TextField(
        value = formState.unit.value,
        onValueChange = { formState.unit.update(it) },
        label = { Text(stringResource(R.string.label_unit).asOptionalLabel()) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.None,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Done
        ),
        modifier = Modifier.width(100.dp)
      )
    }
  }
}