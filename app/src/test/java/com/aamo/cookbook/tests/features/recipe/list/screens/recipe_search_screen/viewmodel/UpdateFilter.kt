package com.aamo.cookbook.tests.features.recipe.list.screens.recipe_search_screen.viewmodel

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipeSearchScreenViewModel
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateFilter : UnconfinedTest() {
  @Test
  fun `filter set`() = runTest {
    val dataFlow = MutableSharedFlow<List<RecipeListRecipeModel>>()
    val viewmodel = RecipeSearchScreenViewModel(fetchData = { dataFlow })

    TestCase.assertEquals(String.EMPTY, viewmodel.nameFilter.value)

    val filter = "222"
    viewmodel.updateFilter(filter)

    TestCase.assertEquals(filter, viewmodel.nameFilter.value)
  }

  @Test
  fun `recipes updated`() = runTest {
    val dataFlow = MutableStateFlow<List<RecipeListRecipeModel>>(emptyList())
    val viewmodel = RecipeSearchScreenViewModel(fetchData = { dataFlow })

    backgroundScope.launch(Dispatchers.Main) {
      viewmodel.recipes.collect()
    }

    assertNotNull(viewmodel.recipes.value)
    assertEquals(emptyList<RecipeListRecipeModel>(), viewmodel.recipes.value)

    val list = listOf(
      RecipeListRecipeModel(recipe = Recipe(name = "1"), rating = null, isBookmarked = false),
      RecipeListRecipeModel(recipe = Recipe(name = "2"), rating = null, isBookmarked = false),
    )
    dataFlow.emit(list)

    assertEquals(list, viewmodel.recipes.value)

    val filter = list[1].recipe.name
    viewmodel.updateFilter(filter)

    assertEquals(
      list.filter { it.recipe.name.contains(filter, ignoreCase = true) }, viewmodel.recipes.value
    )
  }
}