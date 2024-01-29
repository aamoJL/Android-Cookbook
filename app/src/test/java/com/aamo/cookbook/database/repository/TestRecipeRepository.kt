package com.aamo.cookbook.database.repository

import com.aamo.cookbook.Mocker
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRecipeRepository : RecipeRepository {
  object Data {
    val recipes = Mocker.mockRecipeList()
  }

  private var recipes = Data.recipes

  override fun getAllRecipesFlow(): Flow<List<Recipe>> {
    return flow {
      emit(recipes.map { it.value })
    }
  }

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? {
    return recipes.firstOrNull { it.value.id == id }
  }

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    return recipe.value.id
  }

  override suspend fun deleteRecipe(recipe: Recipe) {
    val index = recipes.indexOfFirst { it.value.id == recipe.id }
    if(index != -1){
      recipes.minus(recipes.elementAt(index))
    }
  }

  override fun getFavoriteRecipesFlow(): Flow<List<FullFavoriteRecipe>> {
    TODO("Not yet implemented")
  }

  override suspend fun getFavoriteRecipe(recipeId: Int): FullFavoriteRecipe? {
    TODO("Not yet implemented")
  }

  override suspend fun addRecipeToFavorites(recipeId: Int) {
    TODO("Not yet implemented")
  }

  override suspend fun removeRecipeFromFavorites(recipeId: Int) {
    TODO("Not yet implemented")
  }

  override suspend fun getAllCategories(): List<String> {
    return recipes.map { it.value.category }.distinct()
  }

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> {
    return recipes.map {
      RecipeCategoryTuple(it.value.category, it.value.subCategory)
    }
  }
}