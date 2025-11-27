package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_chapter_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormChapterScreenViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Init {
  @Test
  fun `is new`() {
    val data = RecipeFormChapterFields(name = String.EMPTY)
    val viewmodel = RecipeFormChapterScreenViewModel(formData = data)

    Assert.assertTrue(viewmodel.isNew)
  }

  @Test
  fun `is not new`() {
    val data = RecipeFormChapterFields(name = "Name")
    val viewmodel = RecipeFormChapterScreenViewModel(formData = data)

    Assert.assertFalse(viewmodel.isNew)
  }

  @Test
  fun `form state`() {
    val data =
      RecipeFormChapterFields(name = "Name", note = "Note", steps = listOf(RecipeFormStepFields()))
    val viewmodel = RecipeFormChapterScreenViewModel(formData = data)

    assertEquals(data, viewmodel.getModel())
  }
}