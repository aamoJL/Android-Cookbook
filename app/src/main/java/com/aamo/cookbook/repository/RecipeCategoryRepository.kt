package com.aamo.cookbook.repository

import com.aamo.cookbook.model.RecipeCategory

class RecipeCategoryRepository {
  fun loadCategories(): List<RecipeCategory>{
    return listOf<RecipeCategory>(
      RecipeCategory("Pääruoka"),
      RecipeCategory("Jäkiruoka"),
      RecipeCategory("Muut")
    )
  }
}