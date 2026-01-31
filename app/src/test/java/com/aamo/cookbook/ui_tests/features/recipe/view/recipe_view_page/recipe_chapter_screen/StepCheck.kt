package com.aamo.cookbook.ui_tests.features.recipe.view.recipe_view_page.recipe_chapter_screen

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
@RunWith(RobolectricTestRunner::class)
class StepCheck : PageTest() {
  val recipe = RecipeMocker.getFullMocker().apply {
    modify { it.copy(name = "Recipe") }
    chapters.first().modify { it.copy(name = "Chapter 1") }.steps.first()
      .modify { it.copy(description = "Step 1 description") }
  }.mock()

  @Before
  fun setup() = runTest {
    toRecipeViewPage(recipe)
    waitForLoading()
    rule.onRoot().performTouchInput { swipeLeft() }
  }

  @Test
  fun `toggle state`() {
    val check = rule.onAllNodesWithTag(UITag.CHECK.name, useUnmergedTree = true).onFirst()

    check.assertIsOff().performClick().assertIsOn().performClick().assertIsOff()
  }
}