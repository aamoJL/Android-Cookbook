package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark

suspend fun updateBookmark(
  bookmark: RecipeBookmark,
  value: Boolean,
  addBookmark: suspend (RecipeBookmark) -> Unit,
  removeBookmark: suspend (RecipeBookmark) -> Unit,
) {
  if (value) addBookmark(bookmark) else removeBookmark(bookmark)
}