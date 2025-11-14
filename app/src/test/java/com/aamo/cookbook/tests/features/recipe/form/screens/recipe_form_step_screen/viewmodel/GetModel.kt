package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_step_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormStepScreenViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class GetModel {
  @Test
  fun `returns correct model`() {
    val data = RecipeFormStepFields(
      description = "Desc", timerMinutes = 4, note = "Note", ingredients = listOf(
        RecipeFormIngredientFields()
      )
    )
    val newDesc = "New desc"
    val actual = RecipeFormStepScreenViewModel(formData = data).apply {
      formState.description.update(newDesc)
    }.getModel()

    Assert.assertEquals(data.copy(description = newDesc), actual)
  }
}