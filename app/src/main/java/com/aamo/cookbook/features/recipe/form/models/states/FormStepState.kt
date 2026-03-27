package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.getNewUUID
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import java.util.UUID

class FormStepState(
  val id: UUID,
  val onChange: () -> Unit = {},
  onCanSaveChanged: () -> Unit = {}
) {
  class Fields(val onUnsavedChanges: () -> Unit) {
    val description = ViewModelState(String.EMPTY).onChange { onChange() }
    val timerMinutes = ViewModelState<Int?>(null).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange {
      if (it != null && it > 0) timerFieldToggleValue = true
      onChange()
    }
    val note = ViewModelState(String.EMPTY).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }

    var noteFieldToggleValue by mutableStateOf(false)
    var timerFieldToggleValue by mutableStateOf(false)
    var requiredFieldsFilled by mutableStateOf(false)
      private set

    init {
      noteFieldToggleValue = note.value.isNotEmpty()
      timerFieldToggleValue = timerMinutes.value?.let { it > 0 } ?: false
    }

    private fun onChange() {
      requiredFieldsFilled = requiredFieldsFilled()
      onUnsavedChanges()
    }

    private fun requiredFieldsFilled(): Boolean {
      if (description.value.isBlank()) return false
      if (timerMinutes.value?.let { it < 0 } == true) return false
      return true
    }
  }

  val fields = Fields(onUnsavedChanges = { onChange() })
  val ingredients = ViewModelStateList<FormIngredientState>().onChange { onChange() }
  val canSave = ViewModelState(canSave()).onChange { onCanSaveChanged() }

  fun addIngredient(): FormIngredientState {
    val state = FormIngredientState(
      id = getNewUUID(ingredients.values.map { it.id }),
      onCanSaveChanged = { onChange() },
      onChange = onChange
    )
    ingredients.add(state)
    return state
  }

  private fun onChange() {
    canSave.update(canSave())
    onChange.invoke()
  }

  private fun canSave(): Boolean {
    if (ingredients.values.any { !it.canSave.value }) return false
    return fields.requiredFieldsFilled
  }
}