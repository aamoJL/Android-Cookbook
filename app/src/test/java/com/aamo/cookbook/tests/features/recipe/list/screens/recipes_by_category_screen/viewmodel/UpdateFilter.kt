package com.aamo.cookbook.tests.features.recipe.list.screens.recipes_by_category_screen.viewmodel

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipesByCategoryScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class UpdateFilter {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun after() {
    Dispatchers.resetMain()
  }

  @Test
  fun `filter set`() = runTest {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipesByCategoryScreenViewModel(fetchData = { dataFlow })

    TestCase.assertEquals(String.EMPTY, viewmodel.subCategoryFilter.value)

    val filter = "222"
    viewmodel.updateFilter(filter)

    TestCase.assertEquals(filter, viewmodel.subCategoryFilter.value)
  }

  @Test
  fun `recipes updated`() = runTest {
    val dataFlow = MutableStateFlow<List<RecipeListRecipeModel>>(emptyList())
    val viewmodel = RecipesByCategoryScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(Dispatchers.Main) {
      viewmodel.recipes.collect()
    }

    assertNotNull(viewmodel.recipes.value)
    assertEquals(emptyList<RecipeListRecipeModel>(), viewmodel.recipes.value)

    val list = listOf(
      RecipeListRecipeModel(
        recipe = Recipe(subCategory = "1"), rating = null, isBookmarked = false
      ),
      RecipeListRecipeModel(
        recipe = Recipe(subCategory = "2"), rating = null, isBookmarked = false
      ),
    )
    dataFlow.emit(list)

    assertEquals(list, viewmodel.recipes.value)

    val filter = list[1].recipe.subCategory
    viewmodel.updateFilter(filter)

    assertEquals(
      list.filter { it.recipe.subCategory.contains(filter, ignoreCase = true) },
      viewmodel.recipes.value
    )
  }
}