package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_ingredient_state

import com.aamo.cookbook.features.recipe.form.models.states.FormIngredientState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = FormIngredientState(guid = UUID.randomUUID(), onChange = { called++ })

    state.fields.name.update("Name").also { Assert.assertEquals(1, called) }
    state.fields.amount.update(3.3).also { Assert.assertEquals(2, called) }
    state.fields.unit.update("Unit").also { Assert.assertEquals(3, called) }
  }
}