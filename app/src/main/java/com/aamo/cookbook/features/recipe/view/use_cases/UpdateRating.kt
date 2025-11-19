package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeRating

suspend fun updateRating(
  rating: RecipeRating,
  value: Int?,
  addRating: suspend (RecipeRating) -> Unit,
  removeRating: suspend (RecipeRating) -> Unit,
) {
  if (value == null) removeRating(rating)
  else addRating(
    rating.copy(ratingOutOfFive = value.coerceIn(minimumValue = 0, maximumValue = 5))
  )
}