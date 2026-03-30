package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_step_state

import com.aamo.cookbook.features.recipe.form.models.states.FormIngredientState
import com.aamo.cookbook.features.recipe.form.models.states.FormStepState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = FormStepState(guid = UUID.randomUUID(), onChange = { called++ })

    state.fields.description.update("Desc").also { Assert.assertEquals(1, called) }
    state.fields.timerMinutes.update(3).also { Assert.assertEquals(2, called) }
    state.fields.note.update("Note").also { Assert.assertEquals(3, called) }
    state.ingredientStates.add(
      FormIngredientState(
        guid = UUID.randomUUID(),
        onValidityChanged = {})
    ).also { Assert.assertEquals(4, called) }
  }
}