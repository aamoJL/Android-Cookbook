package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.service.ICalculatorService
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OpenCalculator : UnconfinedTest() {
  @Test
  fun `open called`() = runTest {
    var called = false
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
    )

    viewmodel.openCalculator(calculatorService = object : ICalculatorService {
      override fun open() {
        called = true
      }
    })

    TestCase.assertTrue(called)
  }

  @Test
  fun `returns true when success`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
    )

    val result = viewmodel.openCalculator(calculatorService = object : ICalculatorService {})

    TestCase.assertTrue(result)
  }

  @Test
  fun `returns false when failed`() = runTest {
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(null) } },
      updateBookmark = { _, _ -> TestCase.fail() },
      updateRating = { _, _ -> TestCase.fail() },
    )

    val result = viewmodel.openCalculator(calculatorService = object : ICalculatorService {
      override fun open() {
        error(String.EMPTY)
      }
    })

    TestCase.assertFalse(result)
  }
}