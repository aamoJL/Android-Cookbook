@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.home.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.home.use_cases.fetchRecipeCategoriesFlow
import com.aamo.cookbook.test_utility.database.DatabaseTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FetchRecipeCategories : DatabaseTest() {
  @Test
  fun `returns items in correct order`() = runTest {
    val categories = listOf("Cat 2", "Cat 5", "Cat 1")

    categories.forEach {
      dao.upsert(Recipe(category = it))
    }

    val actual = fetchRecipeCategoriesFlow(dao).first()
    assertEquals(categories.sortedBy { it }, actual)
  }
}