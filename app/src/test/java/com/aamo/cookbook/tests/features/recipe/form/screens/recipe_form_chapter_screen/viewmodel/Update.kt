package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_chapter_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormChapterScreenViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Update {
  @Test
  fun `add new chapter`() {
    val data = RecipeFormChapterFields(name = "Name", note = "Note", steps = listOf())
    val step = RecipeFormStepFields(description = "Step 1")
    val viewmodel = RecipeFormChapterScreenViewModel(formData = data)

    viewmodel.update(step = step)

    val expected = data.steps.plus(step)
    Assert.assertEquals(expected, viewmodel.formState.steps.values)
  }

  @Test
  fun `update chapter`() {
    val data =
      RecipeFormChapterFields(name = "Name", note = "Note", steps = listOf(RecipeFormStepFields()))
    val step = data.steps.first().copy(description = "New desc")
    val viewmodel = RecipeFormChapterScreenViewModel(formData = data)

    viewmodel.update(step = step)

    val expected = data.steps.toMutableList().apply { this[0] = step }
    Assert.assertEquals(expected, viewmodel.formState.steps.values)
  }
}