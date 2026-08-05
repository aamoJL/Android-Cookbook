package com.aamo.cookbook.features.home.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

suspend fun fetchAllRecipes(dao: RecipeDao): List<RecipeWithChaptersStepsAndIngredients> {
  return dao.getCompleteRecipes()
}