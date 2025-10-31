package com.aamo.cookbook.features.recipe.list.use_cases

import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun fetchRecipes(
  fetchRecipe: () -> Flow<List<RecipeWithBookmarkAndRating>>
): Flow<List<RecipeListRecipeModel>> {
  return fetchRecipe().map { list ->
    list.map { (recipe, bookmark, rating) ->
      RecipeListRecipeModel(
        recipe = recipe, rating = rating?.ratingOutOfFive, isBookmarked = bookmark != null
      )
    }
  }
}