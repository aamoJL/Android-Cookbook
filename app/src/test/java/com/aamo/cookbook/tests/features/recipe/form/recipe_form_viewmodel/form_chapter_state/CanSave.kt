package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_chapter_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    RecipeFormViewModel.FormChapterState(onChange = {}).apply {
      name.update("Name")
      addStep()
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave())
    Assert.assertTrue(savableModel().apply { note.update("Note") }.canSave())

    Assert.assertFalse(savableModel().apply { name.update(String.EMPTY) }.canSave())
    Assert.assertFalse(savableModel().apply { steps.removeAt(0) }.canSave())
  }
}