package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

suspend fun saveRecipe(
  dao: RecipeDao,
  recipe: RecipeWithChaptersStepsAndIngredients,
): Long {
  return dao.upsert(recipe = recipe)
}