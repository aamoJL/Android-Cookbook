package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
  fun getAllRecipesFlow(): Flow<List<Recipe>>
  suspend fun getAllCategories(): List<String>
  suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple>
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int
  suspend fun deleteRecipe(recipe: Recipe)
  fun getFavoriteRecipesFlow(): Flow<List<FullFavoriteRecipe>>
  suspend fun getFavoriteRecipe(recipeId: Int): FullFavoriteRecipe?
  suspend fun addRecipeToFavorites(recipeId: Int)
  suspend fun removeRecipeFromFavorites(recipeId: Int)
}

class OfflineRecipeRepository(private val recipeDao: RecipeDao) : RecipeRepository {
  override fun getAllRecipesFlow(): Flow<List<Recipe>> = recipeDao.getRecipesFlow()

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> =
    recipeDao.getCategoriesWithSubcategories()

  override suspend fun getAllCategories(): List<String> = recipeDao.getCategories()

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? =
    recipeDao.getRecipeWithChaptersStepsAndIngredients(id)

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int =
    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe)

  override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)

  override fun getFavoriteRecipesFlow(): Flow<List<FullFavoriteRecipe>> =
    recipeDao.getFavoriteRecipesFlow()

  override suspend fun getFavoriteRecipe(recipeId: Int): FullFavoriteRecipe? =
    recipeDao.getFavoriteRecipe(recipeId)

  override suspend fun addRecipeToFavorites(recipeId: Int) =
    recipeDao.addRecipeToFavorites(FavoriteRecipe(recipeId = recipeId))

  override suspend fun removeRecipeFromFavorites(recipeId: Int){
    getFavoriteRecipe(recipeId)?.also {
      recipeDao.removeRecipeFromFavorites(it.favoriteRecipe)
    }
  }
}