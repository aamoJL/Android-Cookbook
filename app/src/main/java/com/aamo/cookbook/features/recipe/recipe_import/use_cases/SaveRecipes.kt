package com.aamo.cookbook.features.recipe.recipe_import.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

suspend fun saveRecipes(
  dao: RecipeDao,
  recipes: List<RecipeWithChaptersStepsAndIngredients>
): Boolean {
  return dao.upsert(recipes) > 0
}