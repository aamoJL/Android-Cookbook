package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressState : UnconfinedTest() {
  @Test
  fun init() {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
      saveAsCopy = { TestCase.fail() })
    val progress = viewmodel.progressState

    assertEquals(emptyList<List<Boolean>>(), progress.values)
  }

  @Test
  fun `is set when recipe is set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
      saveAsCopy = { TestCase.fail() })
    val progress = viewmodel.progressState

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null))

    assertEquals(recipe.chapters.map { c -> c.steps.map { false } }, progress.values)
  }

  @Test
  fun update() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
      saveAsCopy = { TestCase.fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null))

    val progress = viewmodel.progressState
    val expected = progress.values.toMutableList().apply {
      this[0] = this[0].toMutableList().apply { this[0] = true }
    }
    progress.replaceAt(0, progress.values[0].toMutableList().apply { this[0] = true })

    assertEquals(expected, progress.values)
  }
}