@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.test_utility.database.DatabaseTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeleteRecipe : DatabaseTest() {
  @Test
  fun `recipe deleted`() = runTest {
    val recipe = Recipe().let { result ->
      dao.upsert(recipe = result).let { result.copy(id = it) }
    }

    assertEquals(recipe, dao.getRecipe(recipe.id))

    assertTrue(deleteRecipe(dao = dao, recipe = recipe))
    assertNull(dao.getRecipe(recipe.id))
  }
}