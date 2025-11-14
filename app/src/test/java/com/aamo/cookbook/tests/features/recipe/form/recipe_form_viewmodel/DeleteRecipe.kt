package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteRecipe {
  @Test
  fun `deleteData called`() = runTest {
    var called = false
    RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { null },
      deleteData = { called = true; true }).also {
      it.recipe.drop(1).first()
    }.deleteRecipe()

    assertTrue(called)
  }

  @Test
  fun `returns correct value from deleteData`() = runTest {
    var value = false
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { null },
      deleteData = { value }).also {
      it.recipe.drop(1).first()
    }

    assertFalse(viewmodel.deleteRecipe())
    value = true
    assertTrue(viewmodel.deleteRecipe())
  }

  @Test
  fun `returns false when error on deleteData`() = runTest {
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { null },
      deleteData = { error(String.EMPTY) })

    assertFalse(viewmodel.deleteRecipe())
  }
}