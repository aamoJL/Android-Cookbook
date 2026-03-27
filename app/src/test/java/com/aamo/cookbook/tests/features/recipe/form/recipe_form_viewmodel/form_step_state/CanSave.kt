package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_step_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange = {}).apply {
      description.update("Desc")
      addIngredient()
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave())
    Assert.assertTrue(savableModel().apply { timerMinutes.update(3) }.canSave())
    Assert.assertTrue(savableModel().apply { note.update("Note") }.canSave())
    Assert.assertTrue(savableModel().apply { ingredients.removeAt(0) }.canSave())

    Assert.assertFalse(savableModel().apply { description.update(String.EMPTY) }.canSave())
  }
}