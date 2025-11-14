package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_ingredient_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormIngredientScreenViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class GetModel {
  @Test
  fun `returns correct model`() {
    val data = RecipeFormIngredientFields(name = "Name", amount = 3f, unit = "Unit")
    val newName = "New name"
    val actual = RecipeFormIngredientScreenViewModel(formData = data).apply {
      formState.name.update(newName)
    }.getModel()

    Assert.assertEquals(data.copy(name = newName), actual)
  }
}