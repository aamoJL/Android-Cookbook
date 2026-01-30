package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_ingredient_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormIngredientScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class FormState {
  val data = RecipeFormIngredientFields(name = "Name", amount = 4.5, unit = "Unit")

  @Test
  fun canSave() {
    assertTrue(RecipeFormIngredientScreenViewModel.FormState(formData = data).canSave())

    assertFalse(
      RecipeFormIngredientScreenViewModel.FormState(formData = data.copy(name = String.EMPTY))
        .canSave()
    )

    assertTrue(
      RecipeFormIngredientScreenViewModel.FormState(formData = data.copy(amount = null)).canSave()
    )

    assertFalse(
      RecipeFormIngredientScreenViewModel.FormState(formData = data.copy(amount = -1.0)).canSave()
    )

    assertTrue(
      RecipeFormIngredientScreenViewModel.FormState(formData = data.copy(unit = String.EMPTY))
        .canSave()
    )

    assertFalse(
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { savingState = savingState.getAsSaving() }.canSave()
    )
  }

  @Test
  fun unsavedChanges() {
    assertFalse(
      RecipeFormIngredientScreenViewModel.FormState(formData = data).savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { name.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { amount.update(1.0) }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { unit.update("value") }.savingState.unsavedChanges
    )
  }

  @Test
  fun `amount transformation`() {
    assertEquals(
      null,
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { amount.update(0.0) }.amount.value
    )

    assertEquals(
      null,
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { amount.update(null) }.amount.value
    )

    assertEquals(
      null,
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { amount.update(-1.0) }.amount.value
    )

    assertEquals(
      0.5,
      RecipeFormIngredientScreenViewModel.FormState(formData = data)
        .apply { amount.update(0.5) }.amount.value
    )
  }
}