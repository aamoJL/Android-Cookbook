package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateRating : UnconfinedTest() {
  @Test
  fun `updateRating returns old when updated`() = runTest(UnconfinedTestDispatcher()) {
    var calledValue: Int? = null
    var calledRating: RecipeRating? = null
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { value, rating -> calledValue = value; calledRating = rating },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.rating.collect()
    }

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    val rating = RecipeRating(id = 3L, recipeId = recipe.recipe.id, ratingOutOfFive = 1)
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = rating))

    val expected = 4
    viewmodel.updateRating(expected)

    assertEquals(expected, calledValue)
    assertEquals(rating, calledRating)
  }

  @Test
  fun `updateRating returns old when removed`() = runTest(UnconfinedTestDispatcher()) {
    var calledValue: Int? = null
    var calledRating: RecipeRating? = null
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { value, rating -> calledValue = value; calledRating = rating },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.rating.collect()
    }

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    val rating = RecipeRating(id = 3L, recipeId = recipe.recipe.id, ratingOutOfFive = 1)
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = rating))

    val expected: Int? = null
    viewmodel.updateRating(expected)

    assertEquals(expected, calledValue)
    assertEquals(rating, calledRating)
  }

  @Test
  fun `updateRating returns new when old is null`() = runTest(UnconfinedTestDispatcher()) {
    var calledValue: Int? = null
    var calledRating: RecipeRating? = null
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { value, rating -> calledValue = value; calledRating = rating },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.rating.collect()
    }

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null))

    val expected = 4
    viewmodel.updateRating(expected)

    assertEquals(expected, calledValue)
    assertEquals(RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 0), calledRating)
  }

  @Test
  fun `updateRating not called when not changed`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.rating.collect()
    }

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(), bookmark = null, rating = null
      )
    )

    viewmodel.updateRating(null)
  }
}