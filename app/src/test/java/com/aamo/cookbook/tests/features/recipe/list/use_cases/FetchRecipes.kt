package com.aamo.cookbook.tests.features.recipe.list.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.use_cases.fetchBookmarks
import com.aamo.cookbook.features.recipe.list.use_cases.fetchRecipes
import com.aamo.cookbook.features.recipe.list.use_cases.fetchRecipesByCategory
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FetchRecipes : RecipeDatabaseTest() {
  @Test
  fun `returns correct models`() = runTest {
    val recipes: MutableList<RecipeWithBookmarkAndRating> = mutableListOf()

    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = null)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val rating =
        RecipeRating(recipeId = rId, ratingOutOfFive = 4).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = rating)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
      val rating =
        RecipeRating(recipeId = rId, ratingOutOfFive = 2).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = rating)
    })

    val actual = fetchRecipes(dao).first()
    val expected = recipes.map {
      RecipeListRecipeModel(it.recipe, it.rating?.ratingOutOfFive, it.bookmark != null)
    }

    assertEquals(expected, actual)
  }

  @Test
  fun `returns correct bookmarks`() = runTest {
    val recipes: MutableList<RecipeWithBookmarkAndRating> = mutableListOf()

    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = null)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val rating =
        RecipeRating(recipeId = rId, ratingOutOfFive = 4).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = rating)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
      val rating =
        RecipeRating(recipeId = rId, ratingOutOfFive = 2).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = rating)
    })

    val actual = fetchBookmarks(dao).first()
    val expected = recipes.filter { it.bookmark != null }
      .map { RecipeListRecipeModel(it.recipe, it.rating?.ratingOutOfFive, true) }

    assertEquals(expected, actual)
  }

  @Test
  fun `bookmarks returns empty when no records`() = runTest {
    val actual = fetchBookmarks(dao).first()

    assertEquals(emptyList<RecipeListRecipeModel>(), actual)
  }

  @Test
  fun `returns correct models with category`() = runTest {
    val recipes: MutableList<RecipeWithBookmarkAndRating> = mutableListOf()

    val category = "123"

    recipes.add(
      dao.upsert(RecipeMocker().modify { it.copy(category = category) }.mock())
      .let { rId ->
        val recipe = dao.getRecipe(rId)!!
        RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = null)
      })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
    })
    recipes.add(dao.upsert(RecipeMocker().mock()).let { rId ->
      val recipe = dao.getRecipe(rId)!!
      val rating =
        RecipeRating(recipeId = rId, ratingOutOfFive = 4).let { it.copy(id = dao.upsert(it)) }
      RecipeWithBookmarkAndRating(recipe = recipe, bookmark = null, rating = rating)
    })
    recipes.add(
      dao.upsert(RecipeMocker().modify { it.copy(category = category) }.mock())
      .let { rId ->
        val recipe = dao.getRecipe(rId)!!
        val bookmark = RecipeBookmark(recipeId = rId).let { it.copy(id = dao.upsert(it)) }
        RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = null)
        val rating =
          RecipeRating(recipeId = rId, ratingOutOfFive = 2).let { it.copy(id = dao.upsert(it)) }
        RecipeWithBookmarkAndRating(recipe = recipe, bookmark = bookmark, rating = rating)
      })

    val actual = fetchRecipesByCategory(dao, category = category).first()

    assertEquals(recipes.filter { it.recipe.category == category }.map {
      RecipeListRecipeModel(it.recipe, it.rating?.ratingOutOfFive, it.bookmark != null)
    }, actual)
  }
}