package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao

suspend fun fetchCategorySuggestions(recipeDao: RecipeDao): Map<String, List<String>> {
  return recipeDao.getCategoriesMap().mapValues { (_, value) ->
    value.filter { it.isNotEmpty() }
  }
}