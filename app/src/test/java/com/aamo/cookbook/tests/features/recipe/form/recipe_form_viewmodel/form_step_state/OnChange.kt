package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_step_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel.FormIngredientState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange = { called++ })

    state.description.update("Desc").also { Assert.assertEquals(1, called) }
    state.timerMinutes.update(3).also { Assert.assertEquals(2, called) }
    state.note.update("Note").also { Assert.assertEquals(3, called) }
    state.ingredients.add(FormIngredientState(id = UUID.randomUUID(), onChange = {}))
      .also { Assert.assertEquals(4, called) }
  }
}