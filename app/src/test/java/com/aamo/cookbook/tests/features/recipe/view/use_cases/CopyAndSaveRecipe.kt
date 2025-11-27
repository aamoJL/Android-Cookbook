package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.features.recipe.view.use_cases.copyAndSaveRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
class CopyAndSaveRecipe : RecipeDatabaseTest() {
  @Test
  fun `returns correct id`() = runTest {
    val actual = copyAndSaveRecipe(
      dao = dao, recipe = RecipeMocker.getFullMocker().mock(), nameSuffix = String.EMPTY
    )

    assertEquals(1L, actual)
  }

  @Test
  fun `saves model as copy`() = runTest {
    val nameSuffix = " - copy"
    val unexpected = RecipeMocker.getFullMocker().mock().let {
      dao.upsert(it).let { id ->
        dao.getCompleteRecipe(id)
      }
    }

    checkNotNull(unexpected)

    val actual = copyAndSaveRecipe(dao = dao, recipe = unexpected, nameSuffix = nameSuffix).let {
      dao.getCompleteRecipe(it)
    }

    checkNotNull(actual)

    assertNotEquals(unexpected, actual)
    assertEquals("${unexpected.recipe.name}$nameSuffix", actual.recipe.name)
  }
}