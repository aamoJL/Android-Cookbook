package com.aamo.cookbook.tests.features.recipe.list.screens.recipe_search_screen.viewmodel

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipeSearchScreenViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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
    val viewmodel = RecipeSearchScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipes.collect()
    }

    assertNull(viewmodel.recipes.value)

    val list = listOf(RecipeListRecipeModel(recipe = Recipe(), rating = null, isBookmarked = false))

    dataFlow.emit(list)

    assertEquals(list, viewmodel.recipes.value)
  }
}