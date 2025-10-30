package com.aamo.cookbook.features.recipe.list.use_cases

import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.features.recipe.list.RecipesByCategoryScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun RecipesByCategoryScreenViewModel.Model.Companion.fromDao(
  fetchRecipe: () -> Flow<List<RecipeWithBookmarkAndRating>>
): Flow<List<RecipesByCategoryScreenViewModel.Model>> {
  return fetchRecipe().map { list ->
    list.map { (recipe, bookmark, rating) ->
      RecipesByCategoryScreenViewModel.Model(
        recipe = recipe, rating = rating?.ratingOutOfFive, isBookmarked = bookmark != null
      )
    }
  }
}