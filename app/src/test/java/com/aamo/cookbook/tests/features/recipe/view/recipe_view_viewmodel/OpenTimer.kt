package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.service.ITimerService
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OpenTimer : UnconfinedTest() {
  @Test
  fun `open called`() = runTest {
    var called = false
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
    )

    viewmodel.openTimer(timerService = object : ITimerService {
      override fun open() {
        called = true
      }
    })

    assertTrue(called)
  }

  @Test
  fun `returns true when success`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
    )

    val result = viewmodel.openTimer(timerService = object : ITimerService {})

    assertTrue(result)
  }

  @Test
  fun `returns false when failed`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
    )

    val result = viewmodel.openTimer(timerService = object : ITimerService {
      override fun open() {
        error(String.EMPTY)
      }
    })

    assertFalse(result)
  }
}