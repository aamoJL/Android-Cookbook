package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_chapter_state

import com.aamo.cookbook.features.recipe.form.models.states.FormChapterState
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    FormChapterState(onCanSaveChanged = {}).apply {
      fields.name.update("Name")
      addStep().fields.apply {
        description.update("Desc")
      }
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave.value)
    Assert.assertTrue(savableModel().apply { fields.note.update("Note") }.canSave.value)

    Assert.assertFalse(savableModel().apply { fields.name.update(String.EMPTY) }.canSave.value)
    Assert.assertFalse(savableModel().apply { steps.removeAt(0) }.canSave.value)
  }
}