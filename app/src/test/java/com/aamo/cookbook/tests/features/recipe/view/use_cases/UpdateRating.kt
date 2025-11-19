package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.use_cases.updateRating
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateRating {
  @Test
  fun `addRating called when value is true`() = runTest {
    val rating = RecipeRating(recipeId = 3L, ratingOutOfFive = 1)
    var value: RecipeRating? = null

    updateRating(
      rating = rating,
      value = 1,
      addRating = { value = it },
      removeRating = { TestCase.fail() })

    assertEquals(rating, value)
  }

  @Test
  fun `removeRating called when value is null`() = runTest {
    val rating = RecipeRating(recipeId = 3L, ratingOutOfFive = 1)
    var value: RecipeRating? = null

    updateRating(
      rating = rating,
      value = null,
      addRating = { TestCase.fail() },
      removeRating = { value = it })

    assertEquals(rating, value)
  }
}