package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FetchRecipe : DatabaseTest() {
  @Test
  fun `returns correct model when new`() = runTest {
    val actual = fetchRecipe(dao = dao, recipeId = 0L)

    assertEquals(RecipeWithChaptersStepsAndIngredients(recipe = Recipe()), actual)
  }

  @Test
  fun `returns correct model when existing`() = runTest {
    val model = RecipeMocker.getFullMocker().mock().let {
      dao.upsert(it).let { id ->
        dao.getCompleteRecipe(id)
      }
    }

    checkNotNull(model)

    val actual = fetchRecipe(dao = dao, recipeId = model.recipe.id)

    assertEquals(model, actual)
  }
}