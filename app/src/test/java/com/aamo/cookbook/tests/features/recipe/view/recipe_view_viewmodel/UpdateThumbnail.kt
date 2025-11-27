package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
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
class UpdateThumbnail {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateThumbnail called when changed`() = runTest(UnconfinedTestDispatcher()) {
    var called: String? = null
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
      updateThumbnail = { value, _ -> called = value },
      saveAsCopy = { TestCase.fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    val recipe =
      RecipeMocker.getFullMocker().modify { it.copy(thumbnailUri = String.EMPTY) }.withIds().mock()

    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null))

    val expected = "123"
    viewmodel.updateThumbnail(expected)

    assertEquals(expected, called)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateThumbnail not called when not changed`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
      updateThumbnail = { _, _ -> TestCase.fail() },
      saveAsCopy = { TestCase.fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    val recipe =
      RecipeMocker.getFullMocker().modify { it.copy(thumbnailUri = String.EMPTY) }.withIds().mock()
    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = recipe, bookmark = null, rating = null
      )
    )

    viewmodel.updateThumbnail(recipe.recipe.thumbnailUri)
  }
}