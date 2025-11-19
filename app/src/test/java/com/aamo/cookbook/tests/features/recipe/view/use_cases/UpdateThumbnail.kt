package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.view.use_cases.updateThumbnail
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateThumbnail {
  @Test
  fun `updateRecipe called with correct model`() = runTest {
    var called: Recipe? = null
    val recipe = Recipe()
    val value = "123"

    updateThumbnail(recipe = recipe, value = value, updateRecipe = { called = it })

    TestCase.assertEquals(recipe.copy(thumbnailUri = value), called)
  }
}