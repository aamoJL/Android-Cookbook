package com.aamo.cookbook.features.home.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import kotlinx.coroutines.flow.Flow

// TODO: unit test
fun fetchRecipeCategoriesFlow(recipeDao: RecipeDao): Flow<List<String>> {
  return recipeDao.getCategoriesFlow()
}