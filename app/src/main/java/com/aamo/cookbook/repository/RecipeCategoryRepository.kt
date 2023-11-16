package com.aamo.cookbook.repository

import com.aamo.cookbook.model.RecipeCategory

class RecipeCategoryRepository {
  fun loadCategories(): List<RecipeCategory>{
    return RecipeRepository().loadRecipes()
      .distinctBy { x -> x.category }
      .map { x -> RecipeCategory(x.category) }
  }
}