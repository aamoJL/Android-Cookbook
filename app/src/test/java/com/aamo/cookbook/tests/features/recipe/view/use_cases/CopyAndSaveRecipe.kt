package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.use_cases.copyAndSaveRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class CopyAndSaveRecipe {
  @Test
  fun `returns correct model`() = runTest {
    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    var actual: RecipeWithChaptersStepsAndIngredients? = null

    Assert.assertNotEquals(0L, recipe.recipe.id)

    copyAndSaveRecipe(recipe = recipe, saveCopy = { actual = it })
    val expected =
      RecipeMocker.getFullMocker().modify { it.copy(thumbnailUri = String.EMPTY) }.mock()

    TestCase.assertEquals(expected, actual)
  }
}