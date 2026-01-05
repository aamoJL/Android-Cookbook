package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.features.recipe.form.use_cases.fetchCategorySuggestions
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
class FetchCategorySuggestions : DatabaseTest() {
  @Test
  fun `returns distinct map without empty categories or subcategories`() = runTest {
    listOf(
      RecipeMocker().modify { it.copy(category = "cat 1", subCategory = String.EMPTY) }.mock(),
      RecipeMocker().modify { it.copy(category = "cat 1", subCategory = "sub 1") }.mock(),
      RecipeMocker().modify { it.copy(category = "cat 2", subCategory = "sub 1") }.mock(),
      RecipeMocker().modify { it.copy(category = "cat 2", subCategory = "sub 1") }.mock(),
      RecipeMocker().modify { it.copy(category = "cat 2", subCategory = "sub 2") }.mock(),
      RecipeMocker().modify { it.copy(category = "cat 3", subCategory = String.EMPTY) }.mock(),
      RecipeMocker().modify { it.copy(category = String.EMPTY, subCategory = "sub 1") }.mock(),
    ).forEach { r -> dao.upsert(r) }

    val expected = mapOf(
      "cat 1" to listOf("sub 1"),
      "cat 2" to listOf("sub 1", "sub 2"),
      "cat 3" to emptyList(),
    )
    val actual = fetchCategorySuggestions(recipeDao = dao)

    TestCase.assertEquals(expected, actual)
  }
}