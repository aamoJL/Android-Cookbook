package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface RecipeRepository {
  fun getRecipesFlow(): Flow<List<Recipe>>
  suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple>
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithFavoriteAndRating>>
  suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithFavoriteAndRating?
  suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe?
  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int
  suspend fun deleteRecipe(recipe: Recipe)
  suspend fun addRecipeToFavorites(recipeId: Int)
  suspend fun removeRecipeFromFavorites(recipeId: Int)
  suspend fun upsertRecipeRating(recipeId: Int, rating: Int)
  suspend fun deleteRecipeRating(recipeId: Int)
}

class OfflineRecipeRepository(private val recipeDao: RecipeDao) : RecipeRepository {
  override fun getRecipesFlow(): Flow<List<Recipe>> = recipeDao.getRecipesFlow()

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> =
    recipeDao.getCategoriesWithSubcategories()

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? =
    recipeDao.getRecipeWithChaptersStepsAndIngredients(id)

  override fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithFavoriteAndRating>> =
    recipeDao.getRecipesWithFavoriteAndRatingFlow()

  override suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithFavoriteAndRating? =
    recipeDao.getRecipesWithFavoriteAndRatingFlow().first().firstOrNull { it.recipe.id == recipeId }

  override suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe? =
    recipeDao.getFavoriteRecipeById(recipeId)

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int =
    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe)

  override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

  override suspend fun addRecipeToFavorites(recipeId: Int) =
    recipeDao.addRecipeToFavorites(FavoriteRecipe(recipeId = recipeId))

  override suspend fun removeRecipeFromFavorites(recipeId: Int){
    getFavoriteRecipeById(recipeId)?.also {
      recipeDao.removeRecipeFromFavorites(it.favoriteRecipe)
    }
  }

  override suspend fun upsertRecipeRating(recipeId: Int, rating: Int) {
    val recipeRating = recipeDao.getRecipeRatingById(recipeId)?.copy(ratingOutOfFive = rating)
      ?: RecipeRating(ratingOutOfFive = rating, recipeId = recipeId)
    recipeDao.upsertRecipeRating(recipeRating)
  }

  override suspend fun deleteRecipeRating(recipeId: Int) {
    recipeDao.getRecipeRatingById(recipeId)?.also {
      recipeDao.deleteRecipeRating(it)
    }
  }
}