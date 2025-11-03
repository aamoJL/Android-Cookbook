package com.aamo.cookbook.database.repository

import com.aamo.cookbook.Mocker
import com.aamo.cookbook.database.entities.FullFavoriteRecipe
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeCategoryTuple
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRecipeRepository : RecipeRepository {
  object Data {
    val recipes = Mocker.mockRecipeList()
    val favoriteRecipes = listOf(
      RecipeBookmark(0, 1),
      RecipeBookmark(1, 5),
    )
    val recipeRatings = listOf(
      RecipeRating(0, 2, 1), RecipeRating(1, 5, 2)
    )
  }

  private var recipes = Data.recipes
  private var favorites = Data.favoriteRecipes
  private var ratings = Data.recipeRatings
  override suspend fun getRecipeById(recipeId: Int): Recipe? {
    return recipes.firstOrNull { it.recipe.id == recipeId }?.recipe
  }

  override fun getRecipesFlow(): Flow<List<Recipe>> {
    return flow {
      emit(recipes.map { it.recipe })
    }
  }

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? {
    return recipes.firstOrNull { it.recipe.id == id }
  }

  override fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>> {
    return flow {
      emit(recipes.map { recipe ->
        RecipeWithBookmarkAndRating(
          recipe = recipe.recipe,
          bookmark = favorites.firstOrNull { it.recipeId == recipe.recipe.id },
          rating = ratings.firstOrNull { it.recipeId == recipe.recipe.id })
      })
    }
  }

  override suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithBookmarkAndRating? {
    return recipes.firstOrNull { it.recipe.id == recipeId }?.let { recipe ->
      RecipeWithBookmarkAndRating(
        recipe = recipe.recipe,
        bookmark = favorites.firstOrNull { it.recipeId == recipe.recipe.id },
        rating = ratings.firstOrNull { it.recipeId == recipe.recipe.id })
    }
  }

  override suspend fun upsertRecipeWithChaptersStepsAndIngredients(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    return recipe.recipe.id
  }

  override suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe? {
    return favorites.firstOrNull { it.recipeId == recipeId }?.let { favorite ->
      recipes.firstOrNull { it.recipe.id == recipeId }?.let { recipe ->
        FullFavoriteRecipe(favorite, recipe.recipe)
      }
    }
  }

  override suspend fun upsertRecipe(recipe: Recipe): Int {
    val index = recipes.indexOfFirst { it.recipe == recipe }

    if (index == -1) {
      recipes = recipes.toMutableList().apply { add(RecipeWithChaptersStepsAndIngredients(recipe)) }
    }
    else {
      recipes = recipes.toMutableList().apply {
        this[index] = this[index].copy(recipe = recipe)
      }
    }
    return if (index == -1) 0 else 1
  }

  override suspend fun addRecipeToFavorites(recipeId: Int) {
    favorites = favorites.toMutableList().apply {
      recipes.first { it.recipe.id == recipeId }.also {
        add(RecipeBookmark(id = favorites.maxOf { f -> f.id } + 1, recipeId = it.recipe.id))
      }
    }
  }

  override suspend fun removeRecipeFromFavorites(recipeId: Int) {
    favorites = favorites.toMutableList().apply { remove(first { it.recipeId == recipeId }) }
  }

  override suspend fun upsertRecipeRating(recipeId: Int, rating: Int) {
    val index = ratings.indexOfFirst { it.recipeId == recipeId }

    if (index != -1) {
      ratings =
        ratings.toMutableList().also { it[index] = it[index].copy(ratingOutOfFive = rating) }
    }
    else {
      ratings = ratings.toMutableList().apply {
        add(
          RecipeRating(
            id = ratings.maxOf { it.id }, ratingOutOfFive = rating, recipeId = recipeId
          )
        )
      }
    }
  }

  override suspend fun deleteRecipeRating(recipeId: Int) {
    val index = ratings.indexOfFirst { it.recipeId == recipeId }
    if (index != -1) {
      ratings = ratings.toMutableList().apply { removeAt(index) }
    }
  }

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> {
    return recipes.map {
      RecipeCategoryTuple(it.recipe.category, it.recipe.subCategory)
    }
  }
}