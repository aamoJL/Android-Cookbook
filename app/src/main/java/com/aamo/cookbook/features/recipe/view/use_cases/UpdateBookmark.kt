package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeBookmark

suspend fun updateBookmark(
  dao: RecipeDao,
  bookmark: RecipeBookmark,
  value: Boolean,
) {
  if (value) dao.upsert(bookmark) else dao.delete(bookmark)
}