package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class Init : UnconfinedTest() {
  @Test
  fun `recipe set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
    )
    var actual: RecipeWithChaptersStepsAndIngredients? = null

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect { actual = it }
    }

    assertNull(actual)

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    dataFlow.emit(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null)
    )

    assertEquals(recipe, actual)
  }

  @Test
  fun `servings state set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
    )

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect { }
    }

    val servings = 5
    val recipe =
      RecipeMocker.getFullMocker().modify { it.copy(servings = servings) }.withIds().mock()
    dataFlow.emit(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null)
    )

    assertEquals(servings, viewmodel.servingsState.current.value)
    assertEquals(servings, viewmodel.servingsState.baseline.value)
  }
}