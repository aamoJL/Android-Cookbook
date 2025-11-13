package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class FetchRecipe {
  @Test
  fun `returns correct model`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val actual = fetchRecipe { model }

    assertEquals(model, actual)
  }

  @Test
  fun `RecipeFormInfoFields fromDao returns correct model`() {
    val dao = RecipeMocker.getFullMocker().mock()
    val actual = RecipeFormInfoFields.fromDao(dao = dao)
    val uuid = UUID.randomUUID()

    val expected = RecipeFormInfoFields(
      name = dao.recipe.name,
      category = dao.recipe.category,
      subCategory = dao.recipe.subCategory,
      servings = dao.recipe.servings,
      note = dao.recipe.note,
      chapters = dao.chapters.map { c ->
        RecipeFormChapterFields(
          uuid = uuid, name = c.chapter.name, note = c.chapter.note, steps = c.steps.map { s ->
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
      c.copy(uuid = uuid, steps = c.steps.map { s ->
        s.copy(uuid = uuid, ingredients = s.ingredients.map { i ->
          i.copy(uuid = uuid)
        })
      })
    }))
  }
}