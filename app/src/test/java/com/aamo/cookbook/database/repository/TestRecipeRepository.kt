package com.aamo.cookbook.database.repository

import com.aamo.cookbook.Mocker
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRecipeRepository : RecipeRepository {
  object Data {
    val recipes = Mocker.mockRecipeList()
    val favoriteRecipes = listOf(
      FavoriteRecipe(0, 1),
      FavoriteRecipe(1, 5),
    )
    val recipeRatings = listOf(
      RecipeRating(0, 2, 1),
      RecipeRating(1, 5, 2)
    )
  }

  private var recipes = Data.recipes
  private var favorites = Data.favoriteRecipes
  private var ratings = Data.recipeRatings
  override suspend fun getRecipeById(recipeId: Int): Recipe? {
    return recipes.firstOrNull { it.value.id == recipeId }?.value
  }

  override fun getRecipesFlow(): Flow<List<Recipe>> {
    return flow {
      emit(recipes.map { it.value })
    }
  }

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? {
    return recipes.firstOrNull { it.value.id == id }
  }

  override fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithFavoriteAndRating>> {
    return flow {
      emit(recipes.map { recipe ->
        RecipeWithFavoriteAndRating(
          value = recipe.value,
          favorite = favorites.firstOrNull { it.recipeId == recipe.value.id },
          rating = ratings.firstOrNull { it.recipeId == recipe.value.id }
        )
      })
    }
  }

  override suspend fun getRecipeWithFavoriteAndRating(recipeId: Int): RecipeWithFavoriteAndRating? {
    return recipes.firstOrNull { it.value.id == recipeId }?.let { recipe ->
      RecipeWithFavoriteAndRating(
        value = recipe.value,
        favorite = favorites.firstOrNull { it.recipeId == recipe.value.id },
        rating = ratings.firstOrNull { it.recipeId == recipe.value.id }
      )
    }
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

  override suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe? {
    return favorites.firstOrNull { it.recipeId == recipeId }?.let { favorite ->
      recipes.firstOrNull { it.value.id == recipeId }?.let { recipe ->
        FullFavoriteRecipe(favorite, recipe.value)
      }
    }
  }

  override suspend fun upsertRecipe(recipe: Recipe): Int {
    val index = recipes.indexOfFirst { it.value == recipe }

    if (index == -1) {
      recipes = recipes.toMutableList().apply { add(RecipeWithChaptersStepsAndIngredients(recipe)) }
    } else {
      recipes = recipes.toMutableList().apply {
        this[index] = this[index].copy(value = recipe)
      }
    }
    return if (index == -1) 0 else 1
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

  override suspend fun upsertRecipeRating(recipeId: Int, rating: Int) {
    val index = ratings.indexOfFirst { it.recipeId == recipeId }

    if (index != -1) {
      ratings =
        ratings.toMutableList().also { it[index] = it[index].copy(ratingOutOfFive = rating) }
    } else {
      ratings = ratings.toMutableList().apply {
        add(
          RecipeRating(
            id = ratings.maxOf { it.id },
            ratingOutOfFive = rating,
            recipeId = recipeId
          )
        )
      }
    }
  }

  override suspend fun deleteRecipeRating(recipeId: Int) {
    val index = ratings.indexOfFirst { it.recipeId == recipeId }
    if(index != -1){
      ratings = ratings.toMutableList().apply { removeAt(index) }
    }
  }

  override suspend fun getCategoriesWithSubCategories(): List<RecipeCategoryTuple> {
    return recipes.map {
      RecipeCategoryTuple(it.value.category, it.value.subCategory)
    }
  }
}