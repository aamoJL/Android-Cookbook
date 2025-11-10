package com.aamo.cookbook.tests.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.test_utility.RecipeMocker
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class RecipeDao {
  private lateinit var database: RecipeDatabase
  private lateinit var dao: RecipeDao

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

  // region GET
  @Test
  fun getRecipe() = runTest {
    val recipe = RecipeMocker().mock()

    dao.upsert(recipe).also { id ->
      val expected = recipe.recipe.copy(id = id)
      val actual = dao.getRecipe(id)

      TestCase.assertEquals(expected, actual)
    }
    TestCase.assertNull(dao.getRecipe(0))
  }

  @Test
  fun getCategoriesFlow() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 2).mock(),
    ).also { it.forEach { r -> dao.upsert(r) } }

    val expected = recipes.map { it.recipe.category }.distinct()
    val actual = dao.getCategoriesFlow().first()

    TestCase.assertEquals(expected, actual)
  }

  @Test
  fun getCategoriesMap() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 1).modify { it.copy(subCategory = "sub") }.mock(),
      RecipeMocker(seed = 2).mock(),
    ).also { it.forEach { r -> dao.upsert(r) } }

    val expected = recipes.groupBy({ it.recipe.category }, { it.recipe.subCategory })
    val actual = dao.getCategoriesMap()

    TestCase.assertEquals(expected, actual)
  }

  @Test
  fun getRecipesWithBookmarkAndRatingFlow() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 2).mock(),
      RecipeMocker(seed = 3).mock(),
    ).map { r -> dao.upsert(r).let { r.copy(recipe = r.recipe.copy(id = it)) } }

    val ratings = listOf(
      RecipeRating(ratingOutOfFive = 3, recipeId = recipes[0].recipe.id)
    ).map { r -> dao.upsert(r).let { r.copy(id = it) } }

    val bookmarks = listOf(
      RecipeBookmark(recipeId = recipes[1].recipe.id)
    ).map { b -> dao.upsert(b).let { b.copy(id = it) } }

    val expected = recipes.map { r ->
      RecipeWithBookmarkAndRating(
        recipe = r.recipe,
        bookmark = bookmarks.firstOrNull { it.recipeId == r.recipe.id },
        rating = ratings.firstOrNull { it.recipeId == r.recipe.id })
    }
    val actual = dao.getRecipesWithBookmarkAndRatingFlow().first()

    TestCase.assertEquals(expected, actual)
  }

  @Test
  fun getRecipesWithBookmarkAndRatingFlow_byCategory() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 2).mock(),
      RecipeMocker(seed = 3).mock(),
    ).map { r -> dao.upsert(r).let { r.copy(recipe = r.recipe.copy(id = it)) } }

    val ratings = listOf(
      RecipeRating(ratingOutOfFive = 3, recipeId = recipes[0].recipe.id)
    ).map { r -> dao.upsert(r).let { r.copy(id = it) } }

    val bookmarks = listOf(
      RecipeBookmark(recipeId = recipes[0].recipe.id)
    ).map { b -> dao.upsert(b).let { b.copy(id = it) } }

    val expected = listOf(
      RecipeWithBookmarkAndRating(
        recipe = recipes.first().recipe, bookmark = bookmarks.first(), rating = ratings.first()
      )
    )
    val actual =
      dao.getRecipesWithBookmarkAndRatingFlow(category = recipes.first().recipe.category).first()

    TestCase.assertEquals(expected, actual)
  }

  @Test
  fun getBookmarksWithRatingFlow() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 2).mock(),
      RecipeMocker(seed = 3).mock(),
    ).map { r -> dao.upsert(r).let { r.copy(recipe = r.recipe.copy(id = it)) } }

    val ratings = listOf(
      RecipeRating(ratingOutOfFive = 3, recipeId = recipes.first().recipe.id)
    ).map { r -> dao.upsert(r).let { r.copy(id = it) } }

    val bookmarks = listOf(
      RecipeBookmark(recipeId = recipes.first().recipe.id)
    ).map { b -> dao.upsert(b).let { b.copy(id = it) } }

    val expected = listOf(
      RecipeWithBookmarkAndRating(
        recipe = recipes.first().recipe, bookmark = bookmarks.first(), rating = ratings.first()
      )
    )
    val actual =
      dao.getRecipesWithBookmarkAndRatingFlow(category = recipes.first().recipe.category).first()

    TestCase.assertEquals(expected, actual)
  }

  @Test
  fun getCompleteRecipe() = runTest {
    RecipeMocker.getFullMocker().also { mocker ->
      mocker.mock().also { recipe ->
        dao.upsert(recipe.recipe).also { rId ->
          mocker.modify { it.copy(id = rId) }.chapters.forEach { c ->
            c.mock().also { chapter ->
              dao.upsert(chapter.chapter).also { cId ->
                c.modify { it.copy(id = cId) }.steps.forEach { s ->
                  s.mock().also { step ->
                    dao.upsert(step.step).also { sId ->
                      s.modify { it.copy(id = sId) }.ingredients.forEach { i ->
                        i.mock().also { ingredient -> dao.upsert(ingredient) }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

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

  @Test
  fun getRating() = runTest {
    val recipe = RecipeMocker.getFullMocker().let {
      it.also { dao.upsert(it.mock()) }
    }.withIds().mock().recipe

    val rating = RecipeRating(id = 0, recipeId = recipe.id, ratingOutOfFive = 3).also {
      dao.upsert(it)
    }

    val expected = rating.copy(id = 1)
    val actual = dao.getRating(recipe.id)

    TestCase.assertEquals(expected, actual)
  }

  // endregion

  // region INSERT & UPDATE
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
  // endregion
}