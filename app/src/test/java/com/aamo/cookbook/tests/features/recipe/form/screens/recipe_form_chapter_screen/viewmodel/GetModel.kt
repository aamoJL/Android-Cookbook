package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_chapter_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormChapterScreenViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class GetModel {
  @Test
  fun `returns correct model`() {
    val data =
      RecipeFormChapterFields(name = "Name", note = "Note", steps = listOf(RecipeFormStepFields()))
    val newName = "New name"
    val actual = RecipeFormChapterScreenViewModel(formData = data).apply {
      formState.name.update(newName)
    }.getModel()

    Assert.assertEquals(data.copy(name = newName), actual)
  }
}