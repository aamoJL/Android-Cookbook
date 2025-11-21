package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun fetchRecipe(dao: RecipeDao, recipeId: Long): Flow<RecipeViewRecipeModel> {
  return combine(
    dao.getCompleteRecipeFlow(recipeId = recipeId),
    dao.getBookmarkFlow(recipeId = recipeId),
    dao.getRatingFlow(recipeId = recipeId)
  ) { recipe, bookmark, rating ->
    RecipeViewRecipeModel(
      recipe = recipe ?: throw Error("Failed to fetch recipe"), bookmark = bookmark, rating = rating
    )
  }
}