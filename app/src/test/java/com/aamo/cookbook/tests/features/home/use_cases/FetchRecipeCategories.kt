@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.home.use_cases

import com.aamo.cookbook.features.home.use_cases.fetchRecipeCategoriesFlow
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FetchRecipeCategories {
  @Test
  fun `returns items in correct order`() {
    val categories = listOf("Cat 2", "Cat 5", "Cat 1")
    val result =
      runBlocking { fetchRecipeCategoriesFlow(fetchData = { flow { emit(categories) } }).first() }

    assertEquals(categories.sortedBy { it }, result)
  }
}