package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.entities.FullFavoriteRecipe
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow

// TODO: delete repository
interface RecipeRepository {
  suspend fun getRecipeById(recipeId: Int): Recipe?
  fun getRecipesFlow(): Flow<List<Recipe>>
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>>
  suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithBookmarkAndRating?
  suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe?
  suspend fun upsertRecipe(recipe: Recipe): Int
  suspend fun upsertRecipeWithChaptersStepsAndIngredients(recipe: RecipeWithChaptersStepsAndIngredients): Int
  suspend fun addRecipeToFavorites(recipeId: Int)
  suspend fun removeRecipeFromFavorites(recipeId: Int)
  suspend fun upsertRecipeRating(recipeId: Int, rating: Int)
  suspend fun deleteRecipeRating(recipeId: Int)
}