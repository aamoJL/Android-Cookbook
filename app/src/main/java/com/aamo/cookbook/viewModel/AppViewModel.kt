package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.repository.RecipeRepository
import java.util.UUID

class AppViewModel : ViewModel() {
  private val recipeRepository = RecipeRepository()
  fun getCategories(): List<String> = recipeRepository.getRecipes().distinctBy {
    it.category
  }.map { recipe -> recipe.category }

  fun getRecipes(category: String): List<Recipe> = recipeRepository.getRecipes(category)

  fun getRecipe(id: UUID): Recipe? = recipeRepository.getRecipe(id)

  fun addOrUpdateRecipe(recipe: Recipe): Boolean = recipeRepository.addOrUpdate(recipe)

  fun removeRecipe(id: UUID): Boolean = recipeRepository.removeRecipe(id)
}