package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_chapter_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormChapterScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class FormState {
  val data =
    RecipeFormChapterFields(name = "Name", note = "Note", steps = listOf(RecipeFormStepFields()))

  @Test
  fun canSave() {
    assertTrue(RecipeFormChapterScreenViewModel.FormState(formData = data).canSave())

    assertFalse(
      RecipeFormChapterScreenViewModel.FormState(formData = data.copy(name = String.EMPTY))
        .canSave()
    )

    assertTrue(
      RecipeFormChapterScreenViewModel.FormState(formData = data.copy(note = String.EMPTY))
        .canSave()
    )

    assertFalse(
      RecipeFormChapterScreenViewModel.FormState(formData = data.copy(steps = emptyList()))
        .canSave()
    )

    assertFalse(
      RecipeFormChapterScreenViewModel.FormState(formData = data)
        .apply { savingState = savingState.getAsSaving() }.canSave()
    )
  }

  @Test
  fun unsavedChanges() {
    assertFalse(
      RecipeFormChapterScreenViewModel.FormState(formData = data).savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormChapterScreenViewModel.FormState(formData = data)
        .apply { name.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormChapterScreenViewModel.FormState(formData = data)
        .apply { note.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormChapterScreenViewModel.FormState(formData = data)
        .apply { steps.add(RecipeFormStepFields()) }.savingState.unsavedChanges
    )
  }
}