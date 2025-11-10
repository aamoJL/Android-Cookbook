package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

// TODO: unit test
suspend fun fetchRecipe(
  fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients
): RecipeWithChaptersStepsAndIngredients {
  return fetchData()
}