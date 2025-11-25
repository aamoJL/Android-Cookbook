package com.aamo.cookbook.ui_tests.features.recipe.form.recipe_form_page.existing_recipe

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
class Navigation : PageTest() {
  val recipe = RecipeMocker.getFullMocker().mock()

  @Before
  fun setup() = runTest {
    toRecipeFormPage(recipe = recipe)
    waitForLoading()
  }

  @Test
  fun `on recipeFormInfoScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.screen_title_existing_recipe)).assertExists()
  }

  @Test
  fun `to back`() = runTest {
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.title_ingredients)).waitForDisplayed().assertExists()
    rule.onNodeWithTag(UITag.SCREEN_TITLE.name).assert(hasText(recipe.recipe.name))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `to recipeFormChapterScreen`() = runTest {
    rule.onAllNodesWithTag(UITag.OPTION.name).onFirst().performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_chapter)).assertExists()
  }

  @Test
  fun `to recipeFormStepScreen`() = runTest {
    `to recipeFormChapterScreen`()

    rule.onAllNodesWithTag(UITag.OPTION.name).onFirst().performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_step)).assertExists()
  }

  @Test
  fun `to recipeFormIngredientScreen`() = runTest {
    `to recipeFormStepScreen`()

    rule.onAllNodesWithTag(UITag.OPTION.name).onFirst().performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_ingredient)).assertExists()
  }

  @Test
  fun `back to recipeFormInfoScreen`() = runTest {
    `to recipeFormChapterScreen`()

    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_recipe)).assertExists()
  }

  @Test
  fun `back to recipeFormChapterScreen`() = runTest {
    `to recipeFormStepScreen`()

    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_chapter)).assertExists()
  }

  @Test
  fun `back to recipeFormStepScreen`() = runTest {
    `to recipeFormIngredientScreen`()

    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_existing_step)).assertExists()
  }
}