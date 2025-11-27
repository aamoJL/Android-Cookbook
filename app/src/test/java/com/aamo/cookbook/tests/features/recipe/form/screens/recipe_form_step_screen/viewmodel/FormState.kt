package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_step_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormStepScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class FormState {
  val data = RecipeFormStepFields(
    description = "Desc", timerMinutes = 4, note = "Note", ingredients = listOf(
      RecipeFormIngredientFields()
    )
  )

  @Test
  fun canSave() {
    assertTrue(RecipeFormStepScreenViewModel.FormState(formData = data).canSave())

    assertFalse(
      RecipeFormStepScreenViewModel.FormState(formData = data.copy(description = String.EMPTY))
        .canSave()
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data.copy(timerMinutes = null)).canSave()
    )

    assertFalse(
      RecipeFormStepScreenViewModel.FormState(formData = data.copy(timerMinutes = -1)).canSave()
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data.copy(note = String.EMPTY)).canSave()
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data.copy(ingredients = emptyList()))
        .canSave()
    )

    assertFalse(
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { savingState = savingState.getAsSaving() }.canSave()
    )
  }

  @Test
  fun unsavedChanges() {
    assertFalse(
      RecipeFormStepScreenViewModel.FormState(formData = data).savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { description.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { timerMinutes.update(1) }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { note.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { ingredients.add(RecipeFormIngredientFields()) }.savingState.unsavedChanges
    )
  }

  @Test
  fun `timer minutes transformation`() {
    assertEquals(
      null,
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { timerMinutes.update(0) }.timerMinutes.value
    )

    assertEquals(
      null,
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { timerMinutes.update(null) }.timerMinutes.value
    )

    assertEquals(
      null,
      RecipeFormStepScreenViewModel.FormState(formData = data)
        .apply { timerMinutes.update(-1) }.timerMinutes.value
    )
  }
}