@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import com.aamo.cookbook.test_utility.service.TestPhotoService
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeleteRecipe : RecipeDatabaseTest() {
  @Test
  fun `recipe deleted`() = runTest {
    val recipe = Recipe().let { result ->
      dao.upsert(recipe = result).let { result.copy(id = it) }
    }

    assertEquals(recipe, dao.getRecipe(recipe.id))

    assertTrue(deleteRecipe(dao = dao, photoService = TestPhotoService(), recipe = recipe))
    assertNull(dao.getRecipe(recipe.id))
  }

  @Test
  fun `thumbnail deleted`() = runTest {
    val thumbnail = "123.jpg"
    val recipe = Recipe(thumbnailUri = thumbnail).let { result ->
      dao.upsert(recipe = result).let { result.copy(id = it) }
    }
    var called = false

    assertEquals(recipe, dao.getRecipe(recipe.id))

    assertTrue(deleteRecipe(dao = dao, photoService = object : TestPhotoService() {
      override fun delete(fileName: String): Boolean {
        called = true
        return super.delete(fileName)
      }
    }, recipe = recipe))

    assertTrue(called)
  }

  @Test
  fun `thumbnail not deleted when model not deleted`() = runTest {
    assertFalse(deleteRecipe(dao = dao, photoService = object : TestPhotoService() {
      override fun delete(fileName: String): Boolean {
        fail()
        return super.delete(fileName)
      }
    }, recipe = Recipe()))
  }

  @Test
  fun `thumbnail not deleted when uri is empty`() = runTest {
    val thumbnail = String.EMPTY
    val recipe = Recipe(thumbnailUri = thumbnail).let { result ->
      dao.upsert(recipe = result).let { result.copy(id = it) }
    }

    assertTrue(deleteRecipe(dao = dao, photoService = object : TestPhotoService() {
      override fun delete(fileName: String): Boolean {
        fail()
        return super.delete(fileName)
      }
    }, recipe = recipe))
  }
}