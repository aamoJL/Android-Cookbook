package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.service.ITimerService
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class StartTimer : UnconfinedTest() {
  @Test
  fun `start called`() = runTest {
    var called = false
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    viewmodel.startTimer(
      timerService = object : ITimerService {
        override fun start(title: String, duration: Duration) {
          called = true
        }
      },
      title = String.EMPTY,
      duration = 3.minutes,
    )

    assertTrue(called)
  }

  @Test
  fun `correct title and duration`() = runTest {
    var actualTitle: String? = null
    var actualDuration: Duration? = null
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    val title = "123"
    val duration = 5.minutes

    viewmodel.startTimer(
      timerService = object : ITimerService {
        override fun start(title: String, duration: Duration) {
          actualTitle = title
          actualDuration = duration
        }
      },
      title = title,
      duration = duration,
    )

    assertEquals(title, actualTitle)
    assertEquals(duration, actualDuration!!)
  }

  @Test
  fun `returns true when success`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    val result = viewmodel.startTimer(
      timerService = object : ITimerService {},
      title = String.EMPTY,
      duration = 3.minutes,
    )

    assertTrue(result)
  }

  @Test
  fun `returns false when failed`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    val result = viewmodel.startTimer(
      timerService = object : ITimerService {
        override fun start(title: String, duration: Duration) {
          error(String.EMPTY)
        }
      },
      title = String.EMPTY,
      duration = 3.minutes,
    )

    TestCase.assertFalse(result)
  }
}