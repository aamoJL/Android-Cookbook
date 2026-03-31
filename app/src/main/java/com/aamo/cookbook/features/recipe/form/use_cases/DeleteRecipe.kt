package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe

suspend fun deleteRecipe(
  dao: RecipeDao,
  recipe: Recipe,
): Boolean {
  return dao.delete(recipe) > 0
}