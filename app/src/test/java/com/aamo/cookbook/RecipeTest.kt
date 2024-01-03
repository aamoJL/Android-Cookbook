package com.aamo.cookbook

import com.aamo.cookbook.model.Recipe
import org.junit.Test

class RecipeTest {

  @Test
  fun step_getDescriptionWithFormattedEndChar() {
    val description = "description"
    val stepWithNoIngredients = Recipe.Chapter.Step(description, emptySet())
    val stepWithIngredients = Recipe.Chapter.Step(
      description, setOf(
        Recipe.Ingredient("name", 1f, "unit")
      )
    )

    assert(stepWithNoIngredients.getDescriptionWithFormattedEndChar() == description.plus('.'))
    assert(stepWithIngredients.getDescriptionWithFormattedEndChar() == description.plus(':'))
  }
}