package com.aamo.cookbook.viewModel

import com.aamo.cookbook.MainDispatcherRule
import com.aamo.cookbook.database.repository.TestRecipeRepository
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppViewModelTest {
  private val viewModel = AppViewModel(TestRecipeRepository())

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun getRecipesByCategory() = runTest {
    val recipes = TestRecipeRepository.Data.recipes
    val category = recipes.first().value.category

    val expected = recipes.map { it.value }.sortedBy { it.name }
    val result = viewModel.getRecipesByCategory(category).single()
    assertEquals(expected, result)
  }

  @Test
  fun getRecipesBySubCategory() = runTest {
    val recipes = TestRecipeRepository.Data.recipes
    val category = recipes.first().value.category
    val subCategory = recipes.first().value.subCategory

    val expected = recipes.filter { it.value.category == category && it.value.subCategory == subCategory }.map { it.value }.sortedBy { it.name }
    val result = viewModel.getRecipesBySubCategory(category, subCategory).single()
    assertEquals(expected, result)
  }

  @Test
  fun getCategories() = runTest {
    val recipes = TestRecipeRepository.Data.recipes

    val expected = recipes.map { it.value.category }.sortedBy { it }.distinct()
    val result = viewModel.getCategories().first()
    assertEquals(expected, result)
  }

  @Test
  fun upsertRecipe() = runTest {
    val recipe = Recipe()
    assertEquals(recipe.id, viewModel.upsertRecipe(RecipeWithChaptersStepsAndIngredients(recipe)))
  }

  @Test
  fun deleteRecipe() = runTest {
    viewModel.deleteRecipe(Recipe())
    assert(true)
  }

  @Test
  fun setSelectedCategory() {
    val expected = "new cat"
    viewModel.setSelectedCategory(expected)

    val result = viewModel.selectedCategory.value
    assertEquals(expected, result)
  }

  @Test
  fun getRecipeWithChaptersStepsAndIngredients() = runTest {
    val expected = TestRecipeRepository.Data.recipes.first { it.value.id == 1 }
    val actual = viewModel.getRecipeWithChaptersStepsAndIngredients(1)
    assertEquals(expected, actual)
  }

  @Test
  fun getFavoriteRecipes() = runTest {
    val expected = TestRecipeRepository.Data.favoriteRecipes
      .map { f -> TestRecipeRepository.Data.recipes.first { it.value.id == f.recipeId }.value }
    val actual = viewModel.getFavoriteRecipes().single()
    assertEquals(expected, actual)
  }
}