package com.aamo.cookbook.repository

import com.aamo.cookbook.model.Recipe

class RecipeRepository{

  val recipes = listOf<Recipe>(
  // Pääruoka
  Recipe("Kalakeitto", "Pääruoka", "Keitto"),
  Recipe("Jauhelihakeitto", "Pääruoka", "Keitto"),
  Recipe("Riisipuuro", "Pääruoka", "Puuro"),
  Recipe("Kaurapuuro", "Pääruoka", "Puuro"),

  // Jälkiruoka
  Recipe("Pannukakku", "Jälkiruoka", ""),
  Recipe("Lettu", "Jälkiruoka", ""),
  Recipe("Juustokakku", "Jälkiruoka", "Kakku"),
  Recipe("Täytekakku", "Jälkiruoka", "Kakku"),

  // Kastike
  Recipe("Hollandaisekastike", "Kastike", ""),
  Recipe("Ruskeakastike", "Kastike", ""),

  // Muut
  Recipe("Kermavaahto", "", ""),
  )

  fun loadRecipes(): List<Recipe>{
    return recipes
  }

  fun loadRecipes(category: String): List<Recipe>{
    return recipes.filter { x -> x.category == category }
  }
}