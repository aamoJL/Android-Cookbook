package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_chapter_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel.FormStepState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = RecipeFormViewModel.FormChapterState(onChange = { called++ })

    state.name.update("Name").also { Assert.assertEquals(1, called) }
    state.note.update("Name").also { Assert.assertEquals(2, called) }
    state.steps.add(FormStepState(id = UUID.randomUUID(), onChange = {}))
      .also { Assert.assertEquals(3, called) }
  }
}