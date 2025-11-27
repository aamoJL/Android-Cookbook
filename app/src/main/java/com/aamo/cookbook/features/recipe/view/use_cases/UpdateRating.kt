package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeRating

suspend fun updateRating(
  dao: RecipeDao, rating: RecipeRating, value: Int?
) {
  if (value == null) dao.delete(rating)
  else dao.upsert(rating.copy(ratingOutOfFive = value.coerceIn(minimumValue = 0, maximumValue = 5)))
}