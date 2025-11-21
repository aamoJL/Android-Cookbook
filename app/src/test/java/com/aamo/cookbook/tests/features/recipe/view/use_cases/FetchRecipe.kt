package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.features.recipe.view.use_cases.fetchRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FetchRecipe : RecipeDatabaseTest() {
  @OptIn(ExperimentalCoroutinesApi::class)
  override fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
    super.setup()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun cleanup() {
    Dispatchers.resetMain()
    super.cleanup()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns correct model`() = runTest(UnconfinedTestDispatcher()) {
    val recipe = RecipeMocker.getFullMocker().mock().let {
      dao.upsert(it).let { id ->
        dao.getCompleteRecipe(id)
      }
    }

    checkNotNull(recipe)

    val bookmark = RecipeBookmark(recipeId = recipe.recipe.id).let {
      dao.upsert(it).let { id ->
        dao.getBookmarkFlow(id).first()
      }
    }
    val rating = RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 4).let {
      dao.upsert(it).let { id ->
        dao.getRatingFlow(id).first()
      }
    }

    val actual = fetchRecipe(dao = dao, recipeId = recipe.recipe.id).first()
    val expected = RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating)
    assertEquals(expected, actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns updated model`() = runTest(UnconfinedTestDispatcher()) {
    val recipe = RecipeMocker.getFullMocker().mock().let {
      dao.upsert(it).let { id ->
        dao.getCompleteRecipe(id)
      }
    }

    checkNotNull(recipe)

    val bookmark = RecipeBookmark(recipeId = recipe.recipe.id).let {
      dao.upsert(it).let { id ->
        dao.getBookmarkFlow(id).first()
      }
    }
    val rating = RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 4).let {
      dao.upsert(it).let { id ->
        dao.getRatingFlow(id).first()
      }
    }

    val actualFlow = fetchRecipe(dao = dao, recipeId = recipe.recipe.id)

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      actualFlow.collect()
    }

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating),
      actualFlow.first()
    )

    dao.delete(bookmark!!)

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = rating), actualFlow.first()
    )

    dao.delete(rating!!)

    assertEquals(
      RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null), actualFlow.first()
    )
  }
}