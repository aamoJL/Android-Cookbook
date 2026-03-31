package com.aamo.cookbook.ui_tests.features.recipe.view.recipe_view_page

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.view.components.RecipeViewPagerIndicatorsTags
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.performClickWithKeyboard
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
class Navigation : PageTest() {
  val recipe = RecipeMocker.getFullMocker().apply {
    modify { it.copy(name = "Recipe") }
    chapters.first().modify { it.copy(name = "Chapter 1") }
  }.mock()

  @Before
  fun setup() = runTest {
    toRecipeViewPage(recipe)
    waitForLoading()
  }

  @Test
  fun `on recipeSummaryScreen`() = runTest {
    rule.onNodeWithTag(UITag.PAGE_TITLE.name).assert(hasText(recipe.recipe.name))
    rule.onNodeWithText(getString(R.string.title_ingredients)).assertExists()
  }

  @Test
  fun `to back`() = runTest {
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClickWithKeyboard()
    rule.onNodeWithText(getString(R.string.ph_search)).assertExists()
  }

  @Test
  fun `to recipeFormPage as same`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_more_options)).performClick()
    rule.onNodeWithText(getString(R.string.btn_edit_recipe)).performClick()

    waitForLoading()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name)
      .assert(hasText(getString(R.string.screen_title_edit_recipe)))
  }

  @Test
  fun `to recipeFormPage as copy`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_more_options)).performClick()
    rule.onNodeWithText(getString(R.string.btn_copy_recipe)).performClick()

    waitForLoading()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name)
      .assert(hasText(getString(R.string.screen_title_edit_recipe)))
    rule.onNodeWithText(
      "${recipe.recipe.name}${getString(R.string.suffix_copy, recipe.recipe.name)}"
    ).assertExists()
  }

  @Test
  fun `to recipeChapterScreen with indicator click`() = runTest {
    rule.onNodeWithText(getString(R.string.title_ingredients)).assertIsDisplayed()

    rule.onAllNodesWithTag(
      RecipeViewPagerIndicatorsTags.CHAPTER_INDICATOR.name, useUnmergedTree = true
    ).onFirst().performClick()

    rule.onNodeWithText(getString(R.string.title_ingredients)).assertIsNotDisplayed()
    rule.onNodeWithText(recipe.chapters.first().chapter.name, substring = true).assertExists()
      .assertIsDisplayed()
  }

  @Test
  fun `to recipeChapterScreen with swipe to left`() = runTest {
    rule.onNodeWithText(getString(R.string.title_ingredients)).assertIsDisplayed()

    rule.onRoot().performTouchInput { swipeLeft() }

    rule.onNodeWithText(getString(R.string.title_ingredients)).assertIsNotDisplayed()
    rule.onNodeWithText(recipe.chapters.first().chapter.name, substring = true).assertExists()
      .assertIsDisplayed()
  }

  @Test
  fun `to recipeSummaryScreen with indicator click`() = runTest {
    val text = rule.onNodeWithText(getString(R.string.title_ingredients))

    `to recipeChapterScreen with indicator click`()

    text.assertIsNotDisplayed()

    rule.onNodeWithTag(
      RecipeViewPagerIndicatorsTags.SUMMARY_INDICATOR.name, useUnmergedTree = true
    ).performClick()

    text.assertExists()
    text.assertIsDisplayed()
  }
}