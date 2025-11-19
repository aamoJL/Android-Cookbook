package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.Recipe

suspend fun updateThumbnail(
  recipe: Recipe,
  value: String,
  updateRecipe: suspend (Recipe) -> Unit,
) {
  updateRecipe(recipe.copy(thumbnailUri = value))
}