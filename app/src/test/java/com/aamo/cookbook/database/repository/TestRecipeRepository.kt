package com.aamo.cookbook.database.repository

import com.aamo.cookbook.Mocker
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRecipeRepository : RecipeRepository {
  object Data {
    val recipes = Mocker.mockRecipeList()
    val favoriteRecipes = listOf(
      FavoriteRecipe(0, 1),
      FavoriteRecipe(1, 5),
    )
  }

  private var recipes = Data.recipes
  private var favorites = Data.favoriteRecipes

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
    if (index != -1) {
      recipes.minus(recipes.elementAt(index))
    }
  }

  override fun getFavoriteRecipesFlow(): Flow<List<FullFavoriteRecipe>> {
    return flow {
      emit(favorites.map { favorite ->
        FullFavoriteRecipe(
          favoriteRecipe = favorite,
          recipe = recipes.first { recipe -> recipe.value.id == favorite.recipeId }.value
        )
      })
    }
  }

  override suspend fun getFavoriteRecipe(recipeId: Int): FullFavoriteRecipe? {
    return favorites.firstOrNull { it.recipeId == recipeId }?.let { favorite ->
      recipes.firstOrNull { it.value.id == recipeId }?.let { recipe ->
        FullFavoriteRecipe(favorite, recipe.value)
      }
    }
  }

  override suspend fun addRecipeToFavorites(recipeId: Int) {
    favorites = favorites.toMutableList().apply {
      recipes.first { it.value.id == recipeId }.also {
        add(FavoriteRecipe(id = favorites.maxOf { f -> f.id } + 1, recipeId = it.value.id))
      }
    }
  }

  override suspend fun removeRecipeFromFavorites(recipeId: Int) {
    favorites = favorites.toMutableList().apply { remove(first { it.recipeId == recipeId }) }
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