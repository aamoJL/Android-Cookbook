package com.aamo.cookbook.tests.database.dao.recipe_dao

import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Upsert : DatabaseTest() {
  @Test
  fun upsert_RecipeWithChaptersStepsAndIngredients() = runTest {
    dao.upsert(RecipeMocker.getFullMocker().mock())

    val expected = RecipeMocker.getFullMocker().withIds().mock()
    val actual = dao.getCompleteRecipe(expected.recipe.id)

    checkNotNull(actual)
    TestCase.assertEquals(expected.recipe, actual.recipe)
    expected.chapters.forEachIndexed { iC, ec ->
      TestCase.assertEquals(ec.chapter, actual.chapters[iC].chapter)

      ec.steps.forEachIndexed { iS, es ->
        TestCase.assertEquals(es.step, actual.chapters[iC].steps[iS].step)

        es.ingredients.forEachIndexed { iI, ei ->
          TestCase.assertEquals(ei, actual.chapters[iC].steps[iS].ingredients[iI])
        }
      }
    }
    TestCase.assertEquals(expected, actual)
  }
}