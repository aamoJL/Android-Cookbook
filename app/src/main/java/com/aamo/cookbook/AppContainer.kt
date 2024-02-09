package com.aamo.cookbook

import android.content.Context
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.repository.OfflineRecipeRepository
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.service.IOServiceBase

interface AppContainer {
  val recipeRepository: RecipeRepository
  val ioService: IOServiceBase
}

class AppDataContainer(private val context: Context) : AppContainer {
  override val recipeRepository: RecipeRepository by lazy {
    OfflineRecipeRepository(RecipeDatabase.getDatabase(context).recipeDao())
  }

  override val ioService: IOServiceBase = IOService(context)
}