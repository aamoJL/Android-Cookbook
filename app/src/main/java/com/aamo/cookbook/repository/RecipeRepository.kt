package com.aamo.cookbook.repository

import com.aamo.cookbook.model.Recipe

class RecipeRepository{
  fun loadRecipes(): List<Recipe>{
    return listOf<Recipe>(
      Recipe("Pannukakku"),
      Recipe("Juustokakku"),
      Recipe("Korvapuusti")
    )
  }
}