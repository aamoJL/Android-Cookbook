package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.extensions.load
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test

class Init {
  @Test
  fun `recipe set`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { model },
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() })

    Assert.assertNull(viewmodel.recipe.value)

    viewmodel.recipe.load()

    Assert.assertEquals(model, viewmodel.recipe.value)
  }
}