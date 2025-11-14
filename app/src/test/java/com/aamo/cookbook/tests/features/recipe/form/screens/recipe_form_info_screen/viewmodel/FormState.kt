package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_info_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreenViewModel
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class FormState {
  @Test
  fun canSave() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)

    assertTrue(RecipeFormInfoScreenViewModel.FormState(formData = data).canSave())

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(name = String.EMPTY)).canSave()
    )

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(category = String.EMPTY))
        .canSave()
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(subCategory = String.EMPTY))
        .canSave()
    )

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(servings = 0)).canSave()
    )

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(servings = -1)).canSave()
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(note = String.EMPTY)).canSave()
    )

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data.copy(chapters = emptyList()))
        .canSave()
    )

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { savingState = savingState.getAsSaving() }.canSave()
    )
  }

  @Test
  fun unsavedChanges() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)

    assertFalse(
      RecipeFormInfoScreenViewModel.FormState(formData = data).savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { name.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { category.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { subCategory.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { servings.update(5) }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { note.update("value") }.savingState.unsavedChanges
    )

    assertTrue(
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { chapters.add(RecipeFormChapterFields()) }.savingState.unsavedChanges
    )
  }

  @Test
  fun `servings transformation`() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)

    assertEquals(
      model.recipe.servings, RecipeFormInfoScreenViewModel.FormState(formData = data).servings.value
    )

    assertEquals(
      null,
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { servings.update(0) }.servings.value
    )

    assertEquals(
      null,
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { servings.update(null) }.servings.value
    )

    assertEquals(
      null,
      RecipeFormInfoScreenViewModel.FormState(formData = data)
        .apply { servings.update(-1) }.servings.value
    )
  }
}