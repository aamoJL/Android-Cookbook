package com.aamo.cookbook.database.repository

import com.aamo.cookbook.Mocker
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestRecipeRepository : RecipeRepository {
  object Data {
    val recipes = Mocker.mockRecipeList()
  }

  override fun getAllRecipesStream(): Flow<List<Recipe>> {
    return flow {
      emit(Data.recipes.map { it.value })
    }
  }

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? {
    return Data.recipes.firstOrNull { it.value.id == id }
  }

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    return recipe.value.id
  }

  override suspend fun deleteRecipe(recipe: Recipe) { return }
}