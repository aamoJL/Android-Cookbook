package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class DeleteRecipe : UnconfinedTest() {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `deleteData called`() = runTest(UnconfinedTestDispatcher()) {
    var called = false
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _ -> fail() },
      deleteData = { called = true },
      fetchCategorySuggestions = { emptyMap() },
    )

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    viewmodel.deleteRecipe()

    assertTrue(called)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns correct value from deleteData`() = runTest(UnconfinedTestDispatcher()) {
    val recipe = RecipeMocker.getFullMocker().mock()
    var value: RecipeWithChaptersStepsAndIngredients? = null
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { value = it },
      fetchCategorySuggestions = { emptyMap() },
    )

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    viewmodel.deleteRecipe()

    assertEquals(recipe, value)
  }

  @Test
  fun `does not crash when error`() = runTest {
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _ -> fail() },
      deleteData = { error(String.EMPTY) },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.deleteRecipe()
  }
}