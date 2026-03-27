package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.getNewUUID
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import java.util.UUID

class FormChapterState(val onChange: () -> Unit = {}, onCanSaveChanged: () -> Unit = {}) {
  class Fields(val onUnsavedChanges: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val note = ViewModelState(String.EMPTY).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }

    var noteFieldToggleValue by mutableStateOf(false)
    var changed by mutableStateOf(false)
      private set
    var requiredFieldsFilled by mutableStateOf(false)
      private set

    init {
      noteFieldToggleValue = note.value.isNotEmpty()
    }

    private fun onChange() {
      changed = true
      requiredFieldsFilled = requiredFieldsFilled()
      onUnsavedChanges()
    }

    private fun requiredFieldsFilled(): Boolean {
      return name.value.isNotBlank()
    }
  }

  val fields = Fields(onUnsavedChanges = { onChange() })
  val steps = ViewModelStateList<FormStepState>().onChange { onChange() }
  val canSave = ViewModelState(canSave()).onChange { onCanSaveChanged() }

  var selectedStepId by mutableStateOf<UUID?>(null)

  fun addStep(): FormStepState {
    val state = FormStepState(
      id = getNewUUID(used = steps.values.map { it.id }),
      onCanSaveChanged = { onChange() },
      onChange = onChange
    )
    steps.add(state)
    return state
  }

  private fun onChange() {
    canSave.update(canSave())
    onChange.invoke()
  }

  private fun canSave(): Boolean {
    if (steps.values.isEmpty()) return false
    if (steps.values.any { !it.canSave.value }) return false
    return fields.requiredFieldsFilled
  }
}