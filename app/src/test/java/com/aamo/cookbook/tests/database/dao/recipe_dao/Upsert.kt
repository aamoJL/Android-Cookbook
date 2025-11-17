package com.aamo.cookbook.tests.database.dao.recipe_dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.test_utility.RecipeMocker
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class Upsert {
  private lateinit var database: RecipeDatabase
  private lateinit var dao: com.aamo.cookbook.database.dao.RecipeDao

  @Before
  fun setupDatabase() {
    database = Room.inMemoryDatabaseBuilder(
      context = ApplicationProvider.getApplicationContext(), klass = RecipeDatabase::class.java
    ).build()
    dao = database.recipeDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDatabase() {
    database.close()
  }

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