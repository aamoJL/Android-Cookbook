package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.aamo.cookbook.features.recipe.view.models.ServingsState as state

@OptIn(ExperimentalCoroutinesApi::class)
class ServingsState : UnconfinedTest() {
  @Test
  fun init() {
    val servingsState = state()

    assertEquals(1, servingsState.current.value)
    assertEquals(1, servingsState.baseline.value)
  }

  @Test
  fun `does not change when bookmark or ratings changes`() = runTest {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      saveAsCopy = { fail() })
    val servingsState = viewmodel.servingsState

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    assertEquals(1, servingsState.baseline.value)
    assertEquals(1, servingsState.current.value)

    val servings = 5
    val recipe =
      RecipeMocker.getFullMocker().withIds().modify { it.copy(servings = servings) }.mock()
    dataFlow.emit(RecipeViewRecipeModel(recipe = recipe, bookmark = null, rating = null))

    assertEquals(servings, servingsState.baseline.value)
    assertEquals(servings, servingsState.current.value)

    servingsState.current.update(servings + 1)

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = recipe, bookmark = RecipeBookmark(recipeId = recipe.recipe.id), rating = null
      )
    )

    assertEquals(servings, servingsState.baseline.value)
    assertEquals(servings + 1, servingsState.current.value)

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = recipe,
        bookmark = viewmodel.bookmark.value,
        rating = RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 3)
      )
    )

    assertEquals(servings, servingsState.baseline.value)
    assertEquals(servings + 1, servingsState.current.value)
  }

  @Test
  fun `baseline validation`() {
    val servingsState = state()

    4.also { a ->
      servingsState.baseline.update(a)
      assertEquals(a, servingsState.baseline.value)

      0.also {
        servingsState.baseline.update(it)
        assertEquals(a, servingsState.baseline.value)
      }

      (-1).also {
        servingsState.baseline.update(it)
        assertEquals(a, servingsState.baseline.value)
      }

      1.also {
        servingsState.baseline.update(it)
        assertEquals(it, servingsState.baseline.value)
      }
    }
  }

  @Test
  fun `current validation`() {
    val servingsState = state()

    4.also { a ->
      servingsState.current.update(a)
      assertEquals(a, servingsState.current.value)

      0.also {
        servingsState.current.update(it)
        assertEquals(a, servingsState.current.value)
      }

      (-1).also {
        servingsState.current.update(it)
        assertEquals(a, servingsState.current.value)
      }

      1.also {
        servingsState.current.update(it)
        assertEquals(it, servingsState.current.value)
      }
    }
  }

  @Test
  fun multiplier() {
    val servingsState = state()

    servingsState.baseline.update(5)
    servingsState.current.update(3)

    val expected = servingsState.current.value.toDouble() / servingsState.baseline.value.toDouble()

    assertEquals(expected, servingsState.multiplier, .0)
  }
}