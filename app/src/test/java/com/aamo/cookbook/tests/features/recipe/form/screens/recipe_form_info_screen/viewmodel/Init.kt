package com.aamo.cookbook.tests.features.recipe.form.screens.recipe_form_info_screen.viewmodel

import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreenViewModel
import com.aamo.cookbook.test_utility.extensions.load
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class Init {
  @Test
  fun `is new`() {
    val data = RecipeFormInfoFields(name = String.EMPTY)
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { emptyMap() })

    Assert.assertTrue(viewmodel.isNew)
  }

  @Test
  fun `is not new`() {
    val data = RecipeFormInfoFields(
      name = "Name",
      category = "Cat",
      subCategory = "Sub",
      servings = 3,
      note = "Note",
      chapters = listOf(RecipeFormChapterFields())
    )
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { emptyMap() })

    Assert.assertFalse(viewmodel.isNew)
  }

  @Test
  fun `form state`() {
    val data = RecipeFormInfoFields(
      name = "Name",
      category = "Cat",
      subCategory = "Sub",
      servings = 3,
      note = "Note",
      chapters = listOf(RecipeFormChapterFields())
    )
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { emptyMap() })

    assertEquals(data, viewmodel.getModel())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `category suggestions`() = runTest {
    val data = RecipeFormInfoFields(
      name = "Name",
      category = "Cat",
      subCategory = "Sub",
      servings = 3,
      note = "Note",
      chapters = listOf(RecipeFormChapterFields())
    )
    val expected = mapOf("Cat" to listOf("Sub 1", "Sub 2"))
    val viewmodel =
      RecipeFormInfoScreenViewModel(formData = data, fetchCategorySuggestions = { expected })

    Assert.assertTrue(viewmodel.categorySuggestions.value.isEmpty())

    viewmodel.categorySuggestions.load()

    assertEquals(expected, viewmodel.categorySuggestions.value)
  }
}