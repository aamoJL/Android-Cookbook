package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe

suspend fun deleteRecipe(
  recipe: Recipe,
  deleteThumbnail: suspend (String) -> Unit,
  deleteRecipe: suspend (Recipe) -> Boolean,
): Boolean {
  return deleteRecipe(recipe).also {
    if (recipe.thumbnailUri.isNotEmpty()) deleteThumbnail(recipe.thumbnailUri)
  }
}