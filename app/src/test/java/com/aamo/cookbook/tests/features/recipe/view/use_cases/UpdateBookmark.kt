package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.features.recipe.view.use_cases.updateBookmark
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpdateBookmark : DatabaseTest() {
  @Test
  fun `bookmark added when value is true`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())

    updateBookmark(dao = dao, bookmark = RecipeBookmark(recipeId = recipeId), value = true)

    val actual = dao.getBookmarkFlow(recipeId).first()

    assertNotNull(actual)
  }

  @Test
  fun `bookmark removed when value is false`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())
    val bookmark = dao.upsert(RecipeBookmark(recipeId = recipeId)).let {
      dao.getBookmarkFlow(recipeId).first()
    }

    checkNotNull(bookmark)

    updateBookmark(dao = dao, bookmark = bookmark, value = false)

    val actual = dao.getBookmarkFlow(recipeId).first()

    assertNull(actual)
  }
}