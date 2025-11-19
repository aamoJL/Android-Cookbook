package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpdateRating {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateRating called when changed`() = runTest(UnconfinedTestDispatcher()) {
    var called: Int? = null
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { value, _ -> called = value },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(), bookmark = null, rating = null
      )
    )

    val expected = 4
    viewmodel.updateRating(expected)

    assertEquals(expected, called)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateRating not called when not changed`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(), bookmark = null, rating = null
      )
    )

    viewmodel.updateRating(null)
  }
}