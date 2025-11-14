package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_step_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormStepScreenViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Update {
  @Test
  fun `add new chapter`() {
    val data = RecipeFormStepFields(
      description = "Desc", timerMinutes = 4, note = "Note", ingredients = listOf(
        RecipeFormIngredientFields()
      )
    )
    val ingredient = RecipeFormIngredientFields(name = "Name")
    val viewmodel = RecipeFormStepScreenViewModel(formData = data)

    viewmodel.update(ingredient = ingredient)

    val expected = data.ingredients.plus(ingredient)
    Assert.assertEquals(expected, viewmodel.formState.ingredients.values)
  }

  @Test
  fun `update chapter`() {
    val data = RecipeFormStepFields(
      description = "Desc", timerMinutes = 4, note = "Note", ingredients = listOf(
        RecipeFormIngredientFields()
      )
    )
    val ingredient = data.ingredients.first().copy(name = "New Name")
    val viewmodel = RecipeFormStepScreenViewModel(formData = data)

    viewmodel.update(ingredient = ingredient)

    val expected = data.ingredients.toMutableList().apply { this[0] = ingredient }
    Assert.assertEquals(expected, viewmodel.formState.ingredients.values)
  }
}