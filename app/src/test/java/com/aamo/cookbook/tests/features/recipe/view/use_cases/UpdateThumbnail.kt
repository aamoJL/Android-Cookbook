package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.view.use_cases.updateThumbnail
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateThumbnail {
  @Test
  fun `updateRecipe called with correct model`() = runTest {
    var called: Recipe? = null
    val recipe = Recipe()
    val value = "123"

    updateThumbnail(
      recipe = recipe,
      value = value,
      updateRecipe = { called = it },
      removeThumbnail = {})

    TestCase.assertEquals(recipe.copy(thumbnailUri = value), called)
  }

  @Test
  fun `removeThumbnail called with old thumbnail`() = runTest {
    var called: String? = null
    val recipe = Recipe(thumbnailUri = "123")

    updateThumbnail(
      recipe = recipe,
      value = String.EMPTY,
      updateRecipe = { },
      removeThumbnail = { called = it })

    TestCase.assertEquals(recipe.thumbnailUri, called)
  }
}