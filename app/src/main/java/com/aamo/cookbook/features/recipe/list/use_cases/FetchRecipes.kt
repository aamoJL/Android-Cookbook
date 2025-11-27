package com.aamo.cookbook.features.recipe.list.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun fetchRecipes(dao: RecipeDao): Flow<List<RecipeListRecipeModel>> {
  return dao.getRecipesWithBookmarkAndRatingFlow().map { list ->
    list.map { (recipe, bookmark, rating) ->
      RecipeListRecipeModel(
        recipe = recipe, rating = rating?.ratingOutOfFive, isBookmarked = bookmark != null
      )
    }
  }
}

fun fetchBookmarks(dao: RecipeDao): Flow<List<RecipeListRecipeModel>> {
  return dao.getBookmarksWithRatingFlow().map { list ->
    list.map { (recipe, bookmark, rating) ->
      RecipeListRecipeModel(
        recipe = recipe, rating = rating?.ratingOutOfFive, isBookmarked = bookmark != null
      )
    }
  }
}

fun fetchRecipesByCategory(dao: RecipeDao, category: String): Flow<List<RecipeListRecipeModel>> {
  return dao.getRecipesWithBookmarkAndRatingFlow(category = category).map { list ->
    list.map { (recipe, bookmark, rating) ->
      RecipeListRecipeModel(
        recipe = recipe, rating = rating?.ratingOutOfFive, isBookmarked = bookmark != null
      )
    }
  }
}