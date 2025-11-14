package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_info_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreenViewModel
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Update {
  @Test
  fun `add new chapter`() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)
    val chapter = RecipeFormChapterFields(name = "Chapter 1")
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { emptyMap() })

    viewmodel.update(chapter = chapter)

    val expected = data.chapters.plus(chapter)
    Assert.assertEquals(expected, viewmodel.formState.chapters.values)
  }

  @Test
  fun `update chapter`() {
    val model = RecipeMocker.getFullMocker().mock()
    val data = RecipeFormInfoFields.fromDao(model)
    val chapter = data.chapters.first().copy(name = "New name")
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { emptyMap() })

    viewmodel.update(chapter = chapter)

    val expected = data.chapters.toMutableList().apply { this[0] = chapter }
    Assert.assertEquals(expected, viewmodel.formState.chapters.values)
  }
}