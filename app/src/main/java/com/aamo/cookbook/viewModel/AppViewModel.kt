package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AppViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {
  private var _selectedCategory = MutableStateFlow("")
  val selectedCategory = _selectedCategory.asStateFlow()

  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? =
    recipeRepository.getRecipeWithChaptersStepsAndIngredients(id)

  fun getRecipesByCategory(category: String): Flow<List<Recipe>> =
    recipeRepository.getAllRecipesFlow().map { list ->
      list.filter { it.category == category }
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

  fun getRecipesBySubCategory(category: String, subCategory: String): Flow<List<Recipe>> =
    recipeRepository.getAllRecipesFlow().map { list ->
      list.filter { it.category == category && it.subCategory == subCategory }
        .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }

  fun getCategories(): Flow<List<String>> = recipeRepository.getAllRecipesFlow().map { list ->
    list.map { it.category }.distinct().sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
  }

  fun getFavoriteRecipes(): Flow<List<Recipe>> {
    return recipeRepository.getFavoriteRecipesFlow().map { list ->
      list.map { favoriteRecipe ->
        favoriteRecipe.recipe
      }.sortedWith(compareBy<Recipe, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
        .thenBy(String.CASE_INSENSITIVE_ORDER) { it.name })
    }
  }

  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    return recipeRepository.upsertRecipe(recipe)
  }

  suspend fun deleteRecipe(recipe: Recipe) {
    recipeRepository.deleteRecipe(recipe)
  }

  fun setSelectedCategory(value: String) = _selectedCategory.update { value }
}