package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.extensions.load
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class Init {
  @Test
  fun `recipe set`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { model },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    Assert.assertNull(viewmodel.recipe.value)

    viewmodel.recipe.load()

    assertEquals(model, viewmodel.recipe.value)
  }

  @Test
  fun `is new`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { model },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    Assert.assertTrue(viewmodel.isNew)
  }

  @Test
  fun `is not new`() = runTest {
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = 1L) }.mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { model },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    Assert.assertFalse(viewmodel.isNew)
  }

  @Suppress("HardCodedStringLiteral")
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `category suggestions`() = runTest(timeout = 10.seconds) {
    val expected = mapOf("Cat" to listOf("Sub 1", "Sub 2"))
    val viewmodel = RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { expected },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    Assert.assertTrue(viewmodel.categorySuggestions.value.isEmpty())

    viewmodel.categorySuggestions.load()

    assertEquals(expected, viewmodel.categorySuggestions.value)
  }

  @Test
  fun `form recipe state`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    viewmodel.formRecipeState.value.fields.also { info ->
      assertEquals(recipe.recipe.name, info.name.value)
      assertEquals(recipe.recipe.category, info.category.value)
      assertEquals(recipe.recipe.subCategory, info.subCategory.value)
      assertEquals(recipe.recipe.servings, info.servings.value)
      assertEquals(recipe.recipe.note, info.note.value)
    }
  }

  @Test
  fun `form chapter state`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    assertEquals(3, viewmodel.formRecipeState.value.chapterStates.values.size)
    viewmodel.formRecipeState.value.chapterStates.values.forEachIndexed { i, chapter ->
      assertEquals(recipe.chapters[i].chapter.name, chapter.fields.name.value)
      assertEquals(recipe.chapters[i].chapter.note, chapter.fields.note.value)
    }
  }

  @Test
  fun `form step state`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    val steps = recipe.chapters.flatMap { it.steps }
    val viewmodelSteps =
      viewmodel.formRecipeState.value.chapterStates.values.flatMap { it.stepStates.values }

    assertEquals(4, viewmodelSteps.size)

    viewmodelSteps.forEachIndexed { i, step ->
      assertEquals(steps[i].step.description, step.fields.description.value)
      assertEquals(steps[i].step.timerMinutes, step.fields.timerMinutes.value)
      assertEquals(steps[i].step.note, step.fields.note.value)
    }
  }

  @Test
  fun `form ingredient state`() = runTest {
    val recipe = RecipeMocker.getFullMocker().mock()
    val viewmodel = RecipeFormViewModel(
      fetchData = { recipe },
      saveData = { _ -> fail() },
      deleteData = { fail(); false },
      fetchCategorySuggestions = { emptyMap() },
      deleteThumbnail = { fail() },
      saveThumbnail = { fail(); null },
    )

    viewmodel.recipe.load()

    val ingredients = recipe.chapters.flatMap { it.steps }.flatMap { it.ingredients }
    val viewmodelIngredients =
      viewmodel.formRecipeState.value.chapterStates.values.flatMap { it.stepStates.values }
        .flatMap { it.ingredientStates.values }

    assertEquals(6, viewmodelIngredients.size)

    viewmodelIngredients.forEachIndexed { i, ingredient ->
      assertEquals(ingredients[i].name, ingredient.fields.name.value)
      assertEquals(ingredients[i].amount, ingredient.fields.amount.value)
      assertEquals(ingredients[i].unit, ingredient.fields.unit.value)
    }
  }
}