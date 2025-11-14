package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_info_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreenViewModel
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class GetModel {
  @Test
  fun `returns correct model`() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)
    val newName = "New name"
    val actual = RecipeFormInfoScreenViewModel(
      formData = data, fetchCategorySuggestions = { emptyMap() }).apply {
      formState.name.update(newName)
    }.getModel()

    Assert.assertEquals(data.copy(name = newName), actual)
  }
}