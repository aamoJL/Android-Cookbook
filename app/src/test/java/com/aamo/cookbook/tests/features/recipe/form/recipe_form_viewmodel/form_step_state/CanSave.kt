package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_step_state

import com.aamo.cookbook.features.recipe.form.models.states.FormStepState
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    FormStepState(id = UUID.randomUUID(), onCanSaveChanged = {}).apply {
      fields.description.update("Desc")
      addIngredient().fields.apply {
        name.update("Ing")
      }
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave.value)
    Assert.assertTrue(savableModel().apply { fields.timerMinutes.update(3) }.canSave.value)
    Assert.assertTrue(savableModel().apply { fields.note.update("Note") }.canSave.value)
    Assert.assertTrue(savableModel().apply { ingredients.removeAt(0) }.canSave.value)

    Assert.assertFalse(savableModel().apply { fields.description.update(String.EMPTY) }.canSave.value)
  }
}