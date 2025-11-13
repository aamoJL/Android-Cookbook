package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class SaveRecipe {
  @Test
  fun `saveData called`() = runTest {
    var called = false
    RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { called = true; 1L },
      deleteData = { false }).saveRecipe(RecipeFormInfoFields())

    Assert.assertTrue(called)
  }

  @Test
  fun `passes correct model to saveData`() = runTest {
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = 4, thumbnailUri = "Uri") }.mock()

    var actual: RecipeWithChaptersStepsAndIngredients? = null
    RecipeFormViewModel(
      fetchData = { model },
      saveData = { actual = it; 1L },
      deleteData = { false }).saveRecipe(RecipeFormInfoFields.fromDao(model))

    Assert.assertEquals(model, actual)
  }

  @Test
  fun `returns correct value from saveData`() = runTest {
    val expected = 2L
    val actual = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { expected },
      deleteData = { false }).saveRecipe(RecipeFormInfoFields())

    Assert.assertEquals(expected, actual)
  }

  @Test
  fun `returns null when error`() = runTest {
    val expected = null
    val actual = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { error(String.EMPTY) },
      deleteData = { false }).saveRecipe(RecipeFormInfoFields())

    Assert.assertEquals(expected, actual)
  }
}