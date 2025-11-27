package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_step_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormStepScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Init {
  @Test
  fun `is new`() {
    val data = RecipeFormStepFields(description = String.EMPTY)
    val viewmodel = RecipeFormStepScreenViewModel(formData = data)

    Assert.assertTrue(viewmodel.isNew)
  }

  @Test
  fun `is not new`() {
    val data = RecipeFormStepFields(description = "Desc")
    val viewmodel = RecipeFormStepScreenViewModel(formData = data)

    Assert.assertFalse(viewmodel.isNew)
  }

  @Test
  fun `form state`() {
    val data = RecipeFormStepFields(
      description = "Desc", timerMinutes = 4, note = "Note", ingredients = listOf(
        RecipeFormIngredientFields()
      )
    )
    val viewmodel = RecipeFormStepScreenViewModel(formData = data)

    assertEquals(data, viewmodel.getModel())
  }
}