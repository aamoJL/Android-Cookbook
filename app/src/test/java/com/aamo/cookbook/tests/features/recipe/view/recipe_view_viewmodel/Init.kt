package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Init {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `recipe set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      saveAsCopy = { fail() })
    var actual: RecipeViewRecipeModel? = null

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect { actual = it }
    }

    assertNull(actual)

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    dataFlow.emit(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null)
    )

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null), viewmodel.recipe.value
    )

    val bookmark = RecipeBookmark(recipeId = recipe.recipe.id)
    dataFlow.emit(
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = null)
    )

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = null),
      viewmodel.recipe.value
    )

    val rating = RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 3)
    dataFlow.emit(
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating)
    )

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating),
      viewmodel.recipe.value
    )
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `servings state set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      saveAsCopy = { fail() })

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