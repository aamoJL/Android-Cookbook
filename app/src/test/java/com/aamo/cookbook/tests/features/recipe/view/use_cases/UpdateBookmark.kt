package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.features.recipe.view.use_cases.updateBookmark
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateBookmark {
  @Test
  fun `addBookmark called when value is true`() = runTest {
    val bookmark = RecipeBookmark(recipeId = 3L)
    var value: RecipeBookmark? = null

    updateBookmark(
      bookmark = bookmark,
      value = true,
      addBookmark = { value = it },
      removeBookmark = { TestCase.fail() })

    TestCase.assertEquals(bookmark, value)
  }

  @Test
  fun `removeBookmark called when value is false`() = runTest {
    val bookmark = RecipeBookmark(recipeId = 3L)
    var value: RecipeBookmark? = null

    updateBookmark(
      bookmark = bookmark,
      value = false,
      addBookmark = { TestCase.fail() },
      removeBookmark = { value = it })

    TestCase.assertEquals(bookmark, value)
  }
}