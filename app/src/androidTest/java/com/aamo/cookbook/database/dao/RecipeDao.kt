@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.ui_test_utility.mockers.RecipeMocker
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipeDaoTests {
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

  @Test
  fun getRecipe() = runTest {
    val recipe = RecipeMocker().mock()

    dao.upsert(recipe).also { id ->
      assertEquals(recipe.recipe.copy(id = id), dao.getRecipe(id))
    }
    assertEquals(null, dao.getRecipe(0))
  }

  @Test
  fun getCategoriesFlow() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 2).mock(),
    ).also { it.forEach { r -> dao.upsert(r) } }

    assertEquals(
      recipes.map { it.recipe.category }.distinct(), dao.getCategoriesFlow().first()
    )
  }

  @Test
  fun getCategoriesMap() = runTest {
    val recipes = listOf(
      RecipeMocker(seed = 1).mock(),
      RecipeMocker(seed = 1).modify { it.copy(subCategory = "sub") }.mock(),
      RecipeMocker(seed = 2).mock(),
    ).also { it.forEach { r -> dao.upsert(r) } }

    assertEquals(
      recipes.groupBy({ it.recipe.category }, { it.recipe.subCategory }), dao.getCategoriesMap()
    )
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

    assertEquals(
      recipes.map { r ->
        RecipeWithBookmarkAndRating(
          recipe = r.recipe,
          bookmark = bookmarks.firstOrNull { it.recipeId == r.recipe.id },
          rating = ratings.firstOrNull { it.recipeId == r.recipe.id })
      }, dao.getRecipesWithBookmarkAndRatingFlow().first()
    )
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

    assertEquals(
      listOf(
        RecipeWithBookmarkAndRating(
          recipe = recipes.first().recipe, bookmark = bookmarks.first(), rating = ratings.first()
        )
      ), dao.getRecipesWithBookmarkAndRatingFlow(category = recipes.first().recipe.category).first()
    )
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

    assertEquals(
      listOf(
        RecipeWithBookmarkAndRating(
          recipe = recipes.first().recipe, bookmark = bookmarks.first(), rating = ratings.first()
        )
      ), dao.getRecipesWithBookmarkAndRatingFlow(category = recipes.first().recipe.category).first()
    )
  }

  @Test
  fun getCompleteRecipe() = runTest {
    val expected = RecipeMocker.getFullMocker().let { mocker ->
      dao.upsert(mocker.mock()).let { id ->
        mocker.withIds()
      }
    }.mock()

    val actual = dao.getCompleteRecipe(expected.recipe.id)

    checkNotNull(actual)
    assertEquals(expected.recipe, actual.recipe)
    expected.chapters.forEachIndexed { iC, ec ->
      assertEquals(ec.chapter, actual.chapters[iC].chapter)

      ec.steps.forEachIndexed { iS, es ->
        assertEquals(es.step, actual.chapters[iC].steps[iS].step)

        es.ingredients.forEachIndexed { iI, ei ->
          assertEquals(ei, actual.chapters[iC].steps[iS].ingredients[iI])
        }
      }
    }
    assertEquals(expected, actual)
  }
}