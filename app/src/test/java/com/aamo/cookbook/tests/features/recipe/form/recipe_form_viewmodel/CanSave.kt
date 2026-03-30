package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.ChapterMocker
import com.aamo.cookbook.test_utility.IngredientMocker
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.StepMocker
import com.aamo.cookbook.test_utility.extensions.load
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class CanSave {
  @Test
  fun `form info state`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.validity)

    viewmodel.formRecipeState.value.fields.name.update(String.EMPTY)
    Assert.assertFalse(viewmodel.validity)

    viewmodel.formRecipeState.value.fields.name.update("Name")
    Assert.assertTrue(viewmodel.validity)
  }

  @Test
  fun `chapters size`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.validity)

    viewmodel.formRecipeState.value.chapterStates.clear()
    Assert.assertFalse(viewmodel.validity)
  }

  @Test
  fun `form chapter states`() = runTest {
    val recipe = RecipeMocker().add(ChapterMocker().add(StepMocker())).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.validity)

    viewmodel.formRecipeState.value.chapterStates.values.first().fields.name.update(String.EMPTY)
    Assert.assertFalse(viewmodel.validity)
  }

  @Test
  fun `form step states`() = runTest {
    val recipe = RecipeMocker().add(ChapterMocker().add(StepMocker())).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.validity)

    viewmodel.formRecipeState.value.chapterStates.values.first().stepStates.values.first().fields.description.update(
      String.EMPTY
    )
    Assert.assertFalse(viewmodel.validity)
  }

  @Test
  fun `form ingredient states`() = runTest {
    val recipe =
      RecipeMocker().add(ChapterMocker().add(StepMocker().add(IngredientMocker()))).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.validity)

    viewmodel.formRecipeState.value.chapterStates.values.first().stepStates.values.first().ingredientStates.values.first().fields.name.update(
      String.EMPTY
    )
    Assert.assertFalse(viewmodel.validity)
  }
}