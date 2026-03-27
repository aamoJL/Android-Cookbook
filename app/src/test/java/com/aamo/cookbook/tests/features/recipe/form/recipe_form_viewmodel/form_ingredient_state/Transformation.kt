package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_ingredient_state

import com.aamo.cookbook.features.recipe.form.models.states.FormIngredientState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class Transformation {
  @Test
  fun amount() {
    Assert.assertNull(
      FormIngredientState(UUID.randomUUID()) { }.fields.apply { amount.update(0.0) }.amount.value
    )

    Assert.assertNull(
      FormIngredientState(UUID.randomUUID()) { }.fields.apply { amount.update(null) }.amount.value
    )

    Assert.assertNull(
      FormIngredientState(UUID.randomUUID()) { }.fields.apply { amount.update(-1.0) }.amount.value
    )

    Assert.assertEquals(
      0.5,
      FormIngredientState(UUID.randomUUID()) { }.fields.apply { amount.update(0.5) }.amount.value
    )
  }
}