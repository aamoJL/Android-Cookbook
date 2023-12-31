package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.repository.RecipeRepository
import java.util.UUID

class AppViewModel : ViewModel() {
  fun getCategories(): List<String> = Repositories.recipeRepository.getRecipes().distinctBy {
    it.category
  }.map { recipe -> recipe.category }

  fun getRecipes(category: String): List<Recipe> =
    Repositories.recipeRepository.getRecipes(category)

  fun getRecipe(recipeId: UUID): Recipe? = Repositories.recipeRepository.getRecipe(recipeId)

  object Repositories {
    val recipeRepository = RecipeRepository()
  }
}