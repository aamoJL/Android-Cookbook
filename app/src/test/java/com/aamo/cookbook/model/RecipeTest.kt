package com.aamo.cookbook.model

import com.aamo.cookbook.Mocker
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeTest {
  @Test
  fun step_getDescriptionWithFormattedEndChar() {
    val description = "description"
    val step = Step(description = description)

    assert(step.getDescriptionWithFormattedEndChar(isEmpty = true) == description.plus('.'))
    assert(step.getDescriptionWithFormattedEndChar(isEmpty = false) == description.plus(':'))
  }

  @Test
  fun recipeWithChaptersStepsAndIngredients_copyAsNew() {
    Mocker.mockRecipeList().first().copyAsNew().also { copy ->
      assertEquals(0, copy.value.id)
      assert(copy.chapters.isNotEmpty())

      copy.chapters.forEachIndexed { ci, chapter ->
        assertEquals(0, chapter.value.id)
        assertEquals(ci + 1, chapter.value.orderNumber)
        assert(chapter.steps.isNotEmpty())

        chapter.steps.forEachIndexed { si, step ->
          assertEquals(0, step.value.id)
          assertEquals(si + 1, step.value.orderNumber)
          assert(step.ingredients.isNotEmpty())

          step.ingredients.forEach { ingredient ->
            assertEquals(0, ingredient.id)
          }
        }
      }
    }
  }
}