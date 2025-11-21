package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.use_cases.updateRating
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.RecipeDatabaseTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UpdateRating : RecipeDatabaseTest() {
  @Test
  fun `rating added when value is not null`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())
    val rating = RecipeRating(recipeId = recipeId, ratingOutOfFive = 3)

    val value = 1
    updateRating(dao = dao, rating = rating, value = value)

    val actual = dao.getRatingFlow(recipeId).first()

    assertEquals(value, actual?.ratingOutOfFive)
  }

  @Test
  fun `rating updated when value is not null`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())
    val rating = dao.upsert(RecipeRating(recipeId = recipeId, ratingOutOfFive = 3)).let {
      dao.getRatingFlow(recipeId).first()
    }

    checkNotNull(rating)

    val value = 1
    updateRating(dao = dao, rating = rating, value = value)

    val actual = dao.getRatingFlow(recipeId).first()

    assertEquals(value, actual?.ratingOutOfFive)
  }

  @Test
  fun `rating removed when value is null`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())
    val rating = dao.upsert(RecipeRating(recipeId = recipeId, ratingOutOfFive = 3)).let {
      dao.getRatingFlow(recipeId).first()
    }

    checkNotNull(rating)

    updateRating(dao = dao, rating = rating, value = null)

    val actual = dao.getRatingFlow(recipeId).first()

    assertNull(actual)
  }

  @Test
  fun `rating value clamped to 0 to 5`() = runTest {
    val recipeId = dao.upsert(RecipeMocker().mock())
    val rating = dao.upsert(RecipeRating(recipeId = recipeId, ratingOutOfFive = 3)).let {
      dao.getRatingFlow(recipeId).first()
    }

    checkNotNull(rating)

    updateRating(dao = dao, rating = rating, value = 8)
    assertEquals(5, dao.getRatingFlow(recipeId).first()?.ratingOutOfFive)

    updateRating(dao = dao, rating = rating, value = -7)
    assertEquals(0, dao.getRatingFlow(recipeId).first()?.ratingOutOfFive)
  }
}