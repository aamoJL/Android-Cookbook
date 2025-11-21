package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class DeleteRecipe {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun cleanup() {
    Dispatchers.resetMain()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `deleteData called`() = runTest(UnconfinedTestDispatcher()) {
    var called = false
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _, _, _ -> fail() },
      deleteData = { called = true })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    viewmodel.deleteRecipe()

    assertTrue(called)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns correct value from deleteData`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    var value: RecipeWithChaptersStepsAndIngredients? = null
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _, _, _ -> fail() },
      deleteData = { value = it })

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
      saveData = { _, _, _ -> fail() },
      deleteData = { error(String.EMPTY) })

    viewmodel.deleteRecipe()
  }
}