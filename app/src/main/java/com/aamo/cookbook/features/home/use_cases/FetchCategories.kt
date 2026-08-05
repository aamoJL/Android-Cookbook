package com.aamo.cookbook.features.home.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun fetchRecipeCategoriesFlow(dao: RecipeDao): Flow<List<String>> {
  return dao.getCategoriesFlow().map { list ->
    list.sortedBy { it }
  }
}