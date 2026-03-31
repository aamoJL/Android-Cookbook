package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

suspend fun fetchRecipe(dao: RecipeDao, recipeId: Long): RecipeWithChaptersStepsAndIngredients {
  return if (recipeId == 0L) RecipeWithChaptersStepsAndIngredients(recipe = Recipe())
  else dao.getCompleteRecipe(recipeId) ?: throw Exception("Failed to fetch data")
}