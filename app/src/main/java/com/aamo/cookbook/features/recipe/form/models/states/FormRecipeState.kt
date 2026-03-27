package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList

class FormRecipeState(val onChange: () -> Unit = {}, onCanSaveChanged: () -> Unit = {}) {
  class Fields(val onUnsavedChanges: () -> Unit) {
    val name = ViewModelState(String.EMPTY).onChange { onChange() }
    val category = ViewModelState(String.EMPTY).onChange { onChange() }
    val subCategory = ViewModelState(String.EMPTY).onChange { onChange() }
    val servings = ViewModelState<Int?>(1).transformation { value ->
      if (value != null && value < 1) null else value
    }.onChange { onChange() }
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
      if (name.value.isBlank()) return false
      if (category.value.isBlank()) return false
      servings.value.also { if (it == null || it < 1) return false }
      return true
    }
  }

  val fields = Fields(onUnsavedChanges = { onChange() })
  val chapterStates = ViewModelStateList<FormChapterState>().onChange { onChange() }
  val canSave = ViewModelState(canSave()).onChange { onCanSaveChanged() }

  fun addChapter(): FormChapterState {
    return FormChapterState(onCanSaveChanged = { onChange() }, onChange = onChange).also {
      chapterStates.add(it)
    }
  }

  private fun onChange() {
    canSave.update(canSave())
    onChange.invoke()
  }

  private fun canSave(): Boolean {
    if (chapterStates.values.isEmpty()) return false
    if (chapterStates.values.any { !it.canSave.value }) return false
    return fields.requiredFieldsFilled
  }
}