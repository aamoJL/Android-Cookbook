package com.aamo.cookbook.tests.features.recipe.list.screens.recipes_by_category_screen.viewmodel

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipesByCategoryScreenViewModel
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Init {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `recipes set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipesByCategoryScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipes.collect()
    }

    assertNull(viewmodel.recipes.value)

    val list = listOf(RecipeListRecipeModel(recipe = Recipe(), rating = null, isBookmarked = false))

    dataFlow.emit(list)

    assertEquals(list, viewmodel.recipes.value)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `subcategories set`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipesByCategoryScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.subCategories.collect()
    }

    assertTrue(viewmodel.subCategories.value.isEmpty())

    val list = listOf(
      RecipeListRecipeModel(
        recipe = Recipe(subCategory = "1"), rating = null, isBookmarked = false
      ),
      RecipeListRecipeModel(
        recipe = Recipe(subCategory = "2"), rating = null, isBookmarked = false
      ),
    )

    dataFlow.emit(list)

    assertEquals(list.map { it.recipe.subCategory }, viewmodel.subCategories.value)
  }
}