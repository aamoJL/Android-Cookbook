package com.aamo.cookbook.model

import org.junit.Test

class RecipeTest {

  @Test
  fun step_getDescriptionWithFormattedEndChar() {
    val description = "description"
    val step = Step(description = description)

    assert(step.getDescriptionWithFormattedEndChar(isEmpty = true) == description.plus('.'))
    assert(step.getDescriptionWithFormattedEndChar(isEmpty = false) == description.plus(':'))
  }
}