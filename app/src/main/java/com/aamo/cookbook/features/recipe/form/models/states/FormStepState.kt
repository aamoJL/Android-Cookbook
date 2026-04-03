package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.utility.extensions.general.getUniqueUUID
import com.aamo.cookbook.utility.extensions.general.getUniqueUUIDs
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import java.util.UUID

class FormStepState(
  private val model: StepWithIngredients = StepWithIngredients(step = Step()),
  val guid: UUID,
  val onChange: () -> Unit = {},
  onValidityChanged: () -> Unit = {},
) {
  class Fields(private val model: Step = Step(), private val onChange: () -> Unit) {
    val description = ViewModelState(model.description).onChange { onChange() }
    val timerMinutes =
      ViewModelState(model.timerMinutes).transformation { value -> if (value != null && value < 1) null else value }
        .onChange {
          if (it != null && it > 0) timerFieldToggleValue = true
          onChange()
        }
    val note = ViewModelState(model.note).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }

    var noteFieldToggleValue by mutableStateOf(model.note.isNotEmpty())
    var timerFieldToggleValue by mutableStateOf(model.timerMinutes?.let { it > 0 } ?: false)
    var requiredFieldsFilled by mutableStateOf(requiredFieldsFilled())
      private set

    private fun onChange() {
      requiredFieldsFilled = requiredFieldsFilled()
      onChange.invoke()
    }

    private fun requiredFieldsFilled(): Boolean {
      if (description.value.isBlank()) return false
      if (timerMinutes.value?.let { it < 0 } == true) return false
      return true
    }

    fun getModel(): Step {
      return model.copy(
        description = description.value,
        timerMinutes = timerMinutes.value,
        note = note.value,
      )
    }
  }

  val fields = Fields(model = model.step, onChange = { onChange() })
  val ingredientStates = ViewModelStateList(model.ingredients.let { ingredients ->
    val guids = getUniqueUUIDs(count = ingredients.size).toList()

    ingredients.mapIndexed { i, ingredient ->
      createIngredientState(model = ingredient, guid = guids[i])
    }
  }).onChange { onChange() }
  val validity = ViewModelState(checkValidity()).onChange { onValidityChanged() }

  fun addIngredient(): FormIngredientState {
    return createIngredientState(
      model = Ingredient(),
      guid = getUniqueUUID(ingredientStates.values.map { it.guid }),
    ).also {
      ingredientStates.add(it)
    }
  }

  private fun createIngredientState(model: Ingredient, guid: UUID): FormIngredientState {
    return FormIngredientState(
      model = model, guid = guid, onValidityChanged = { onChange() }, onChange = onChange
    )
  }

  fun getModel(): StepWithIngredients {
    return model.copy(
      step = fields.getModel(),
      ingredients = ingredientStates.values.map { it.getModel() },
    )
  }

  private fun onChange() {
    validity.update(checkValidity())
    onChange.invoke()
  }

  private fun checkValidity(): Boolean {
    if (ingredientStates.values.any { !it.validity.value }) return false
    return fields.requiredFieldsFilled
  }
}