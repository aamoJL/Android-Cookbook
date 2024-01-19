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

  private var _selectedSubCategory = MutableStateFlow("")
  val selectedSubCategory = _selectedSubCategory.asStateFlow()

  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int) : RecipeWithChaptersStepsAndIngredients? =
    recipeRepository.getRecipeWithChaptersStepsAndIngredients(id)

  fun getRecipesByCategory(category: String): Flow<List<Recipe>> = recipeRepository.getAllRecipesStream().map {  list ->
    list.filter { it.category == category }.sortedBy { it.name }
  }

  fun getRecipesBySubCategory(category: String, subCategory: String): Flow<List<Recipe>> = recipeRepository.getAllRecipesStream().map {  list ->
    list.filter { it.category == category && it.subCategory == subCategory }.sortedBy { it.name }
  }

  fun getCategories(): Flow<List<String>> = recipeRepository.getAllRecipesStream().map { list ->
    list.map { it.category }.distinct().sortedBy { it }
  }

  fun getSubCategories(category: String): Flow<List<String>> = recipeRepository.getAllRecipesStream().map { list ->
    list.filter { it.category == category }.map { it.subCategory }.distinct().sortedBy { it }
  }

  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int {
    return recipeRepository.upsertRecipe(recipe)
  }

  suspend fun deleteRecipe(recipe: Recipe) {
    recipeRepository.deleteRecipe(recipe)
  }

  fun setSelectedCategory(value: String) = _selectedCategory.update { value }

  fun setSelectedSubCategory(value: String) = _selectedSubCategory.update { value }
}