package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

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

  @Test
  fun `RecipeFormInfoFields fromDao returns correct model`() {
    val model = RecipeMocker.getFullMocker().mock()
    val actual = RecipeFormInfoFields.fromDao(model = model)
    val uuid = UUID.randomUUID()

    val expected = RecipeFormInfoFields(
      name = model.recipe.name,
      category = model.recipe.category,
      subCategory = model.recipe.subCategory,
      servings = model.recipe.servings,
      note = model.recipe.note,
      chapters = model.chapters.map { c ->
        RecipeFormChapterFields(
          name = c.chapter.name, note = c.chapter.note, steps = c.steps.map { s ->
            RecipeFormStepFields(
              uuid = uuid,
              description = s.step.description,
              timerMinutes = s.step.timerMinutes,
              note = s.step.note,
              ingredients = s.ingredients.map { i ->
                RecipeFormIngredientFields(
                  uuid = uuid, name = i.name, amount = i.amount, unit = i.unit
                )
              })
          })
      })

    assertEquals(expected, actual.copy(chapters = actual.chapters.map { c ->
      c.copy(steps = c.steps.map { s ->
        s.copy(uuid = uuid, ingredients = s.ingredients.map { i ->
          i.copy(uuid = uuid)
        })
      })
    }))
  }
}