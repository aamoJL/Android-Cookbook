package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_chapter_state

import com.aamo.cookbook.features.recipe.form.models.states.FormChapterState
import com.aamo.cookbook.features.recipe.form.models.states.FormStepState
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = FormChapterState(onChange = { called++ })

    state.fields.name.update("Name").also { Assert.assertEquals(1, called) }
    state.fields.note.update("Note").also { Assert.assertEquals(2, called) }
    state.stepStates.add(FormStepState(guid = UUID.randomUUID(), onValidityChanged = {}))
      .also { Assert.assertEquals(3, called) }
  }
}