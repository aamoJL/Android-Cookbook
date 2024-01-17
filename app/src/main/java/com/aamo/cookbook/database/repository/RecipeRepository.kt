package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
  fun getAllRecipesStream(): Flow<List<Recipe>>
  suspend fun getAllCategories(): List<String>
  suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple>
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int
  suspend fun deleteRecipe(recipe: Recipe)
}

class OfflineRecipeRepository(private val recipeDao: RecipeDao) : RecipeRepository {
  override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getRecipes()

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> =
    recipeDao.getCategoriesWithSubcategories()

  override suspend fun getAllCategories(): List<String> = recipeDao.getCategories()

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? =
    recipeDao.getRecipeWithChaptersStepsAndIngredients(id)

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int =
    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe)

  override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
}