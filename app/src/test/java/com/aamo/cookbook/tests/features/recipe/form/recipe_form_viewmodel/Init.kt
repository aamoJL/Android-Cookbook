package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert
import org.junit.Test

class Init {
  @Test
  fun `recipe set`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val viewmodel =
      RecipeFormViewModel(fetchData = { model }, saveData = { null }, deleteData = { false })

    while (viewmodel.isLoading) yield()

    Assert.assertEquals(model, viewmodel.recipe)
  }

  @Test
  fun `isLoading state`() = runTest {
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { null },
      deleteData = { false })

    Assert.assertTrue(viewmodel.isLoading)
    while (viewmodel.isLoading) yield()
    Assert.assertFalse(viewmodel.isLoading)
  }
}