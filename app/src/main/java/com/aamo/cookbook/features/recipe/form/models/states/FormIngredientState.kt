package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import java.util.UUID

class FormIngredientState(
  val id: UUID,
  val onChange: () -> Unit = {},
  onCanSaveChanged: () -> Unit = {},
) {
  class Fields(val onUnsavedChanges: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val amount = ViewModelState<Double?>(null).transformation { value ->
      if (value != null && value <= 0) null else value
    }.onChange { onChange() }
    val unit = ViewModelState(String.EMPTY).onChange { onChange() }

    var requiredFieldsFilled by mutableStateOf(false)
      private set

    private fun onChange() {
      requiredFieldsFilled = requiredFieldsFilled()
      onUnsavedChanges()
    }

    private fun requiredFieldsFilled(): Boolean {
      if (name.value.isBlank()) return false
      if (amount.value?.let { it < 0 } == true) return false
      return true
    }
  }

  val fields = Fields(onUnsavedChanges = { onChange() })
  val canSave = ViewModelState(canSave()).onChange { onCanSaveChanged() }

  private fun onChange() {
    canSave.update(canSave())
    onChange.invoke()
  }

  private fun canSave(): Boolean {
    return fields.requiredFieldsFilled
  }
}