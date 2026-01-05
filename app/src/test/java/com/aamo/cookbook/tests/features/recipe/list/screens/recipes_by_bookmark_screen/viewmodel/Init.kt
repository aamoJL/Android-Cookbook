package com.aamo.cookbook.tests.features.recipe.list.screens.recipes_by_bookmark_screen.viewmodel

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipesByBookmarkScreenViewModel
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class Init : UnconfinedTest() {
  @Test
  fun `recipes set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipesByBookmarkScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipes.collect()
    }

    assertNull(viewmodel.recipes.value)

    val list = listOf(RecipeListRecipeModel(recipe = Recipe(), rating = null, isBookmarked = false))

    dataFlow.emit(list)

    assertEquals(list, viewmodel.recipes.value)
  }

  @Test
  fun `categories set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipesByBookmarkScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.categories.collect()
    }

    assertTrue(viewmodel.categories.value.isEmpty())

    val list = listOf(
      RecipeListRecipeModel(recipe = Recipe(category = "1"), rating = null, isBookmarked = false),
      RecipeListRecipeModel(recipe = Recipe(category = "2"), rating = null, isBookmarked = false),
    )

    dataFlow.emit(list)

    assertEquals(list.map { it.recipe.category }, viewmodel.categories.value)
  }
}