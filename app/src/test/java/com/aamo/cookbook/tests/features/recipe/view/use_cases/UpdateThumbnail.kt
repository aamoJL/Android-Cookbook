package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.features.recipe.view.use_cases.updateThumbnail
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import com.aamo.cookbook.test_utility.service.TestPhotoService
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpdateThumbnail : RecipeDatabaseTest() {
  @Test
  fun `thumbnail updated when value is not null`() = runTest {
    val recipe = dao.upsert(RecipeMocker().mock()).let {
      dao.getRecipe(it)
    }

    checkNotNull(recipe)

    val thumbnail = "123.jpg"
    updateThumbnail(
      dao = dao, photoService = TestPhotoService(), recipe = recipe, value = thumbnail
    )

    val actual = dao.getRecipe(recipe.id)

    assertEquals(recipe.copy(thumbnailUri = thumbnail), actual)
  }

  @Test
  fun `thumbnail removed when value is empty`() = runTest {
    val recipe =
      dao.upsert(RecipeMocker().modify { it.copy(thumbnailUri = "123.jpg") }.mock()).let {
        dao.getRecipe(it)
      }

    checkNotNull(recipe)

    var deleted = false
    val thumbnail = String.EMPTY
    updateThumbnail(
      dao = dao, photoService = object : TestPhotoService() {
        override fun delete(fileName: String): Boolean {
          deleted = true
          return super.delete(fileName)
        }
      }, recipe = recipe, value = thumbnail
    )

    val actual = dao.getRecipe(recipe.id)

    assertEquals(recipe.copy(thumbnailUri = thumbnail), actual)
    assertTrue(deleted)
  }
}