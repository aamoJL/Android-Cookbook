package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_ingredient_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormIngredientScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Init {
  @Test
  fun `is new`() {
    val data = RecipeFormIngredientFields(name = String.EMPTY)
    val viewmodel = RecipeFormIngredientScreenViewModel(formData = data)

    Assert.assertTrue(viewmodel.isNew)
  }

  @Test
  fun `is not new`() {
    val data = RecipeFormIngredientFields(name = "Name")
    val viewmodel = RecipeFormIngredientScreenViewModel(formData = data)

    Assert.assertFalse(viewmodel.isNew)
  }

  @Test
  fun `form state`() {
    val data = RecipeFormIngredientFields(name = "Name", amount = 3f, unit = "Unit")
    val viewmodel = RecipeFormIngredientScreenViewModel(formData = data)

    assertEquals(data, viewmodel.getModel())
  }
}