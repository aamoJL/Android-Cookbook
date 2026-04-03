package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.utility.extensions.general.getUniqueUUID
import com.aamo.cookbook.utility.extensions.general.getUniqueUUIDs
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import java.util.UUID

class FormChapterState(
  private val model: ChapterWithStepsAndIngredients = ChapterWithStepsAndIngredients(chapter = Chapter()),
  val onChange: () -> Unit = {},
  onValidityChanged: () -> Unit = {},
) {
  class Fields(private val model: Chapter = Chapter(), private val onChange: () -> Unit) {
    val name = ViewModelState(model.name).onChange { onChange() }
    val note = ViewModelState(model.note).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }

    var noteFieldToggleValue by mutableStateOf(model.note.isNotEmpty())
    var isDirty by mutableStateOf(false)
      private set
    var requiredFieldsFilled by mutableStateOf(requiredFieldsFilled())
      private set

    private fun onChange() {
      isDirty = true
      requiredFieldsFilled = requiredFieldsFilled()
      onChange.invoke()
    }

    private fun requiredFieldsFilled(): Boolean {
      return name.value.isNotBlank()
    }

    fun getModel(): Chapter {
      return model.copy(
        name = name.value,
        note = note.value,
      )
    }
  }

  val fields = Fields(model = model.chapter, onChange = { onChange() })
  val stepStates = ViewModelStateList(model.steps.let { steps ->
    val guids = getUniqueUUIDs(count = steps.size).toList()

    steps.mapIndexed { i, step ->
      createStepState(model = step, guid = guids[i])
    }
  }).onChange { onChange() }
  val validity = ViewModelState(checkValidity()).onChange { onValidityChanged() }

  var selectedStepId by mutableStateOf<UUID?>(null)

  fun addStep(): FormStepState {
    return createStepState(
      model = StepWithIngredients(step = Step()),
      guid = getUniqueUUID(used = stepStates.values.map { it.guid })
    ).also {
      stepStates.add(it)
    }
  }

  private fun createStepState(model: StepWithIngredients, guid: UUID): FormStepState {
    return FormStepState(
      model = model, guid = guid, onValidityChanged = { onChange() }, onChange = onChange
    )
  }

  fun getModel(): ChapterWithStepsAndIngredients {
    return model.copy(
      chapter = fields.getModel(),
      steps = stepStates.values.map { it.getModel() },
    )
  }

  private fun onChange() {
    validity.update(checkValidity())
    onChange.invoke()
  }

  private fun checkValidity(): Boolean {
    if (stepStates.values.isEmpty()) return false
    if (stepStates.values.any { !it.validity.value }) return false
    return fields.requiredFieldsFilled
  }
}