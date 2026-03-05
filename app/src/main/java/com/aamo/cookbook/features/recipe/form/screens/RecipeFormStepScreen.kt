package com.aamo.cookbook.features.recipe.form.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.components.FormList
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.components.inputs.BasicDismissibleItem
import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.ui.components.inputs.text_field.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.modals.UnsavedDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.tags.UITag
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormStepScreen(val index: Int)

class RecipeFormStepScreenViewModel(private val formData: RecipeFormStepFields) : ViewModel() {
  class FormState(formData: RecipeFormStepFields) {
    val description = ViewModelState(formData.description).onChange { onUnsavedChanges() }
    val timerMinutes = ViewModelState(formData.timerMinutes).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange { onUnsavedChanges() }
    val note = ViewModelState(formData.note).onChange { onUnsavedChanges() }
    val ingredients = ViewModelStateList(formData.ingredients).onChange { onUnsavedChanges() }
    var savingState by mutableStateOf(SavingState())

    fun canSave(): Boolean {
      if (savingState.state == SavingState.State.SAVING) return false
      if (description.value.isEmpty()) return false
      if (timerMinutes.value?.let { it < 0 } == true) return false
      return true
    }

    private fun onUnsavedChanges() {
      savingState = savingState.copy(unsavedChanges = true)
    }
  }

  val formState = FormState(formData)
  val isNew = formData.description.isEmpty()

  fun update(ingredient: RecipeFormIngredientFields) {
    formState.ingredients.values.indexOfFirst { it.uuid == ingredient.uuid }.also { index ->
      if (index == -1) formState.ingredients.add(ingredient)
      else formState.ingredients.replaceAt(index, ingredient)
    }
  }

  fun getModel(): RecipeFormStepFields {
    return formState.let {
      formData.copy(
        description = it.description.value,
        timerMinutes = it.timerMinutes.value,
        note = it.note.value,
        ingredients = it.ingredients.values
      )
    }
  }
}

fun NavGraphBuilder.recipeFormStepScreen(
  formData: (index: Int) -> RecipeFormStepFields,
  onSubmit: (RecipeFormStepFields) -> Unit,
  onBack: () -> Unit,
  enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
  exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
  popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
  popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
) {
  composable<RecipeFormStepScreen>(
    enterTransition = enterTransition,
    exitTransition = exitTransition,
    popEnterTransition = popEnterTransition,
    popExitTransition = popExitTransition
  ) { navStack ->
    val (index) = navStack.toRoute<RecipeFormStepScreen>()
    val viewmodel: RecipeFormStepScreenViewModel = viewModel(factory = viewModelFactory {
      initializer { RecipeFormStepScreenViewModel(formData = formData(index)) }
    })

    val stepNavController = rememberNavController()

    NavHost(
      navController = stepNavController, startDestination = RecipeFormStepScreen(index = index)
    ) {
      composable<RecipeFormStepScreen>(
        enterTransition = { null },
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = { null }) {
        RecipeFormStepScreenContent(
          formState = viewmodel.formState,
          isNew = viewmodel.isNew,
          stepIndex = index,
          onNewIngredient = {
            stepNavController.navigate(RecipeFormIngredientScreen(index = viewmodel.formState.ingredients.values.size))
          },
          onEditIngredient = { stepNavController.navigate(RecipeFormIngredientScreen(index = it)) },
          onDeleteIngredient = { viewmodel.formState.ingredients.remove(it) },
          onSubmit = { onSubmit(viewmodel.getModel()) },
          onBack = onBack,
        )
      }
      recipeFormIngredientScreen(
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        formData = { index ->
          viewmodel.formState.ingredients.values.elementAtOrElse(index) { RecipeFormIngredientFields() }
        },
        onSubmit = { ingredient ->
          viewmodel.update(ingredient)
          stepNavController.navigateUp()
        },
        onBack = { stepNavController.navigateUp() })
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
  onDeleteIngredient: (RecipeFormIngredientFields) -> Unit,
  onSubmit: () -> Unit,
  onBack: () -> Unit,
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
        true -> stringResource(R.string.screen_title_new_step)
        else -> stringResource(R.string.screen_title_existing_step)
      },
      onBack = { if (formState.savingState.unsavedChanges) openUnsavedDialog = true else onBack() },
      actions = {
        IconButton(onClick = onSubmit, enabled = formState.canSave()) {
          Icon(
            painter = painterResource(R.drawable.rounded_check_24),
            contentDescription = stringResource(R.string.cd_save)
          )
        }
      })
  }) {
    BackgroundSurface(
      modifier = Modifier
        .padding(it)
        .fillMaxSize()
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(8.dp)) {
        StepForm(formState = formState, orderNumber = stepIndex + 1)
        StepFormIngredientList(
          ingredients = formState.ingredients.values,
          onNewIngredient = onNewIngredient,
          onEditIngredient = onEditIngredient,
          onDeleteIngredient = onDeleteIngredient,
        )
      }
    }
  }
}

@Composable
private fun StepForm(
  formState: RecipeFormStepScreenViewModel.FormState,
  orderNumber: Int,
) {
  Column {
    HorizontalDividerLabel(
      label = stringResource(R.string.title_step_information, orderNumber), modifier = Modifier.padding(12.dp)
    )
    ElevatedCard(
      shape = RoundedCornerShape(8.dp), colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
      )
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .padding(8.dp)
          .padding(bottom = 4.dp)
          .fillMaxWidth()
      ) {
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
        NumberField(
          value = formState.timerMinutes.value,
          onValueChange = { formState.timerMinutes.update(it) },
          validator = NullableIntFieldValidator,
          label = { Text(stringResource(R.string.label_step_timer).asOptionalLabel()) },
          shape = RectangleShape,
          suffix = { Text(text = stringResource(R.string.suffix_minutes)) },
          colors = borderlessTextFieldColors(),
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
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StepFormIngredientList(
  ingredients: List<RecipeFormIngredientFields>,
  onNewIngredient: () -> Unit,
  onEditIngredient: (index: Int) -> Unit,
  onDeleteIngredient: (RecipeFormIngredientFields) -> Unit,
) {
  FormList(
    title = stringResource(R.string.title_ingredients),
    actions = {
      OutlinedIconButton(
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant),
        onClick = onNewIngredient,
        colors = IconButtonDefaults.outlinedIconButtonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
      ) {
        Icon(
          painter = painterResource(R.drawable.rounded_add_24),
          contentDescription = stringResource(R.string.cd_form_add_new_item),
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
    },
  ) {
    LazyColumn {
      itemsIndexed(
        items = ingredients,
        key = { _, ingredient -> ingredient.uuid },
      ) { index, ingredient ->
        Column(modifier = Modifier.animateItem()) {
          IngredientListItem(
            ingredient = ingredient,
            onClick = { onEditIngredient(index) },
            onDismiss = { onDeleteIngredient(ingredient) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag(UITag.OPTION.name)
          )

          if (index != ingredients.size - 1) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .2f))
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
  modifier: Modifier = Modifier
) {
  BasicDismissibleItem(dismissAction = onDismiss, modifier = modifier) {
    ListItem(
      modifier = Modifier.clickable { onClick() },
      headlineContent = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = if (ingredient.amount == Double.Zero || ingredient.amount == null) "" else ingredient.amount.toFractionFormattedString(),
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
    )
  }
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeFormStepScreenContent(
      formState = RecipeFormStepScreenViewModel.FormState(
        formData = RecipeFormStepFields(
          description = "Step 1", timerMinutes = 15, ingredients = listOf(
            RecipeFormIngredientFields(name = "Ingredient 1", amount = 2.5, unit = "kg")
          )
        )
      ),
      isNew = true,
      stepIndex = 0,
      onNewIngredient = {},
      onEditIngredient = {},
      onDeleteIngredient = {},
      onSubmit = {},
      onBack = {},
    )
  }
}