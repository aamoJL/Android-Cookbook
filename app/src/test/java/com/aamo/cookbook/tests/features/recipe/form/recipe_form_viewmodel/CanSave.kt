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
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.canSave)

    viewmodel.formRecipeState.fields.name.update(String.EMPTY)
    Assert.assertFalse(viewmodel.canSave)

    viewmodel.formRecipeState.fields.name.update("Name")
    Assert.assertTrue(viewmodel.canSave)
  }

  @Test
  fun `chapters size`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.canSave)

    viewmodel.formRecipeState.chapterStates.clear()
    Assert.assertFalse(viewmodel.canSave)
  }

  @Test
  fun `form chapter states`() = runTest {
    val recipe = RecipeMocker().add(ChapterMocker().add(StepMocker())).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.canSave)

    viewmodel.formRecipeState.chapterStates.values.first().fields.name.update(String.EMPTY)
    Assert.assertFalse(viewmodel.canSave)
  }

  @Test
  fun `form step states`() = runTest {
    val recipe = RecipeMocker().add(ChapterMocker().add(StepMocker())).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.canSave)

    viewmodel.formRecipeState.chapterStates.values.first().steps.values.first().fields.description.update(
      String.EMPTY
    )
    Assert.assertFalse(viewmodel.canSave)
  }

  @Test
  fun `form ingredient states`() = runTest {
    val recipe =
      RecipeMocker().add(ChapterMocker().add(StepMocker().add(IngredientMocker()))).mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _, _, _ -> fail() },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.canSave)

    viewmodel.formRecipeState.chapterStates.values.first().steps.values.first().ingredients.values.first().fields.name.update(
      String.EMPTY
    )
    Assert.assertFalse(viewmodel.canSave)
  }
}