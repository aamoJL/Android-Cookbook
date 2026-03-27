package com.aamo.cookbook.ui_tests.features.recipe.view.recipe_view_page.recipe_summary_screen

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.extensions.general.toStringWithoutZero
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
@RunWith(RobolectricTestRunner::class)
class ServingsCount : PageTest() {
  val servings = 123
  val recipe = RecipeMocker.getFullMocker().apply {
    modify { it.copy(name = "Recipe", servings = servings) }
    chapters.first().modify { it.copy(name = "Chapter 1") }.steps.first().ingredients.first()
      .modify { it.copy(amount = servings * 100.0) }
  }.mock()

  @Before
  fun setup() = runTest {
    toRecipeViewPage(recipe)
    waitForLoading()
  }

  @Test
  fun `servings counter`() {
    rule.onNodeWithText(recipe.recipe.servings.toString()).assertExists()

    rule.onNodeWithContentDescription(getString(R.string.cd_increase_value), useUnmergedTree = true)
      .performClick()

    rule.onNodeWithText((recipe.recipe.servings + 1).toString()).assertExists()

    rule.onNodeWithContentDescription(getString(R.string.cd_decrease_value), useUnmergedTree = true)
      .performClick().performClick()

    rule.onNodeWithText((recipe.recipe.servings - 1).toString()).assertExists()
  }

  @Test
  fun `decrease value disabled when servings equals 1`() {
    rule.onNodeWithContentDescription(getString(R.string.cd_decrease_value), useUnmergedTree = true)
      .apply {
        onParent().assertIsEnabled()
        repeat(recipe.recipe.servings - 1) { performClick() }
        onParent().assertIsNotEnabled()
      }
  }

  @Test
  fun `ingredient amount change`() {
    rule.onNodeWithText((servings * 100.0).toStringWithoutZero()).assertExists()

    rule.onNodeWithContentDescription(getString(R.string.cd_increase_value), useUnmergedTree = true)
      .performClick()

    rule.onNodeWithText(((servings + 1) * 100.0).toStringWithoutZero()).assertExists()
  }
}