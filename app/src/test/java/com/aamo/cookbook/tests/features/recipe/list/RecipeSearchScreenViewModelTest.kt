@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.recipe.list

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.screens.RecipeSearchScreenViewModel
import com.aamo.cookbook.test_utility.MainDispatcherRule
import com.aamo.cookbook.test_utility.Mocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeSearchScreenViewModelTest {
  private lateinit var viewmodel: RecipeSearchScreenViewModel
  private val source = MutableSharedFlow<List<RecipeListRecipeModel>>()

  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() = runTest {
    viewmodel = RecipeSearchScreenViewModel(fetchData = { source })
  }

  @Test
  fun verifyInitWord() {
    val viewModel = RecipeSearchScreenViewModel(fetchData = { flow { emit(emptyList()) } })
    val expected = String.Companion.EMPTY
    val actual = viewModel.filterWord.value
    Assert.assertEquals(expected, actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun verifyInitValidRecipes() = runTest {
    val models = Mocker.Companion.mockRecipeList().map { it.recipe }.map {
      RecipeListRecipeModel(recipe = it, isBookmarked = true, rating = 3)
    }

    var result = listOf<RecipeListRecipeModel>()

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipes.collect { result = it }
    }

    assert(result.isEmpty())

    source.emit(models)

    Assert.assertEquals(models, result)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun updateFilter() = runTest {
    val recipe =
      RecipeListRecipeModel(recipe = Recipe(name = "Name"), isBookmarked = true, rating = 3)

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipes.collect { }
    }

    source.emit(listOf(recipe))

    Assert.assertEquals(1, viewmodel.recipes.value.size)
    Assert.assertEquals(recipe.recipe, viewmodel.recipes.value.first().recipe)

    viewmodel.updateFilter("123")

    Assert.assertEquals(0, viewmodel.recipes.value.size)

    viewmodel.updateFilter(recipe.recipe.name)

    Assert.assertEquals(1, viewmodel.recipes.value.size)
    Assert.assertEquals(recipe.recipe, viewmodel.recipes.value.first().recipe)
  }
}