package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

// TODO: delete repository
interface RecipeRepository {
  suspend fun getRecipeById(recipeId: Int): Recipe?
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithBookmarkAndRating?
  suspend fun upsertRecipe(recipe: Recipe): Int
  suspend fun addRecipeToFavorites(recipeId: Int)
  suspend fun removeRecipeFromFavorites(recipeId: Int)
  suspend fun upsertRecipeRating(recipeId: Int, rating: Int)
  suspend fun deleteRecipeRating(recipeId: Int)
}