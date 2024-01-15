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

  private var recipes = Data.recipes

  override fun getAllRecipesStream(): Flow<List<Recipe>> {
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
}