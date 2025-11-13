@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class DeleteRecipe {
  @Test
  fun `deleteRecipe called with correct model`() = runTest {
    val model = Recipe(id = 3)
    var actual: Recipe? = null

    deleteRecipe(recipe = model, deleteThumbnail = {}) { actual = it; true }

    assertEquals(model, actual)
  }

  @Test
  fun `deleteThumbnail called on deletion with correct thumbnail uri`() = runTest {
    val model = Recipe(id = 3, thumbnailUri = "Uri")
    var actual: String? = null

    assertTrue(model.thumbnailUri.isNotEmpty())

    deleteRecipe(recipe = model, deleteThumbnail = { actual = it }) { true }

    assertEquals(model.thumbnailUri, actual)
  }

  @Test
  fun `deleteThumbnail not called when model not deleted`() = runTest {
    val model = Recipe(id = 3, thumbnailUri = "Uri")

    deleteRecipe(recipe = model, deleteThumbnail = { fail() }) { false }
  }

  @Test
  fun `deleteThumbnail not called when uri is empty`() = runTest {
    val model = Recipe(id = 3, thumbnailUri = String.EMPTY)

    assertTrue(model.thumbnailUri.isEmpty())
    deleteRecipe(recipe = model, deleteThumbnail = { fail() }) { true }
  }

  @Test
  fun `returns true when model deleted`() = runTest {
    val model = Recipe(id = 3)

    assertTrue(deleteRecipe(recipe = model, deleteThumbnail = {}) { true })
  }

  @Test
  fun `returns false when model not deleted`() = runTest {
    val model = Recipe(id = 3)

    assertFalse(deleteRecipe(recipe = model, deleteThumbnail = {}) { false })
  }
}