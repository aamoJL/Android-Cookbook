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

  fun getRecipe(id: UUID): Recipe? = Repositories.recipeRepository.getRecipe(id)

  fun addOrUpdateRecipe(recipe: Recipe): Boolean = Repositories.recipeRepository.addOrUpdate(recipe)

  fun removeRecipe(id: UUID): Boolean = Repositories.recipeRepository.removeRecipe(id)

  object Repositories {
    val recipeRepository = RecipeRepository()
  }
}