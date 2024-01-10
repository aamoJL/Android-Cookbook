package com.aamo.cookbook

import android.content.Context
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.repository.OfflineRecipeRepository
import com.aamo.cookbook.database.repository.RecipeRepository

interface AppContainer {
  val recipeRepository: RecipeRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
  override val recipeRepository: RecipeRepository by lazy {
    OfflineRecipeRepository(RecipeDatabase.getDatabase(context).recipeDao())
  }
}