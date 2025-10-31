package com.aamo.cookbook.features.home.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun fetchRecipeCategoriesFlow(fetchData: () -> Flow<List<String>>): Flow<List<String>> {
  return fetchData().map { list ->
    list.sortedBy { it }
  }
}