package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
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

  fun getCategories(): Flow<List<String>> = recipeRepository.getRecipesFlow().map { list ->
    list.map { it.category }.distinct().sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it })
  }

  fun getRecipesWithFavoriteAndRatingByCategory(category: String): Flow<List<RecipeWithFavoriteAndRating>> =
    recipeRepository.getRecipesWithFavoriteAndRatingFlow().map { list ->
      list.filter { it.value.category == category }
    }

  fun getRecipesWithFavoriteAndRatingByFavorite(): Flow<List<RecipeWithFavoriteAndRating>> =
    recipeRepository.getRecipesWithFavoriteAndRatingFlow().map { list ->
      list.filter { it.favorite != null }
    }

  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    return recipeRepository.upsertRecipe(recipe)
  }

  suspend fun deleteRecipe(recipe: Recipe) {
    recipeRepository.deleteRecipe(recipe)
  }

  fun setSelectedCategory(value: String) = _selectedCategory.update { value }
}