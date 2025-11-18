package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class ServingsState {
  @Test
  fun init() {
    val servingsState = RecipeViewViewModel.ServingsState()

    assertEquals(1, servingsState.current.value)
    assertEquals(1, servingsState.baseline.value)
  }

  @Test
  fun `baseline validation`() {
    val servingsState = RecipeViewViewModel.ServingsState()

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
    val servingsState = RecipeViewViewModel.ServingsState()

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
    val servingsState = RecipeViewViewModel.ServingsState()

    servingsState.baseline.update(5)
    servingsState.current.update(3)

    val expected = servingsState.current.value.toDouble() / servingsState.baseline.value.toDouble()

    assertEquals(expected, servingsState.multiplier, .0)
  }
}