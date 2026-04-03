package com.aamo.cookbook.ui_tests.features.recipe.form.recipe_form_page.existing_recipe

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
class EditRecipeForm : PageTest() {
  val recipe = RecipeMocker.getFullMocker().mock()

  @Before
  fun setup() = runTest {
    toRecipeFormPage(recipe = recipe)
    waitForLoading()
  }

  @Test
  fun submit() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsEnabled()

    rule.onNodeWithText(getString(R.string.label_name)).performTextReplacement("New Recipe")

    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick()

    // on RecipeViewPage
    rule.onNodeWithText(getString(R.string.title_ingredients)).waitForDisplayed().assertExists()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name).assert(hasText("New Recipe"))
  }

  @Test
  fun submit_correct_backstack() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsEnabled()

    rule.onNodeWithText(getString(R.string.label_name)).performTextReplacement("New Recipe")

    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick()

    // on RecipeViewPage
    rule.onNodeWithText(getString(R.string.title_ingredients)).waitForDisplayed().assertExists()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name).assert(hasText("New Recipe"))

    // back
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_new_recipe)).assertDoesNotExist()
    rule.onNodeWithText(getString(R.string.screen_title_edit_recipe)).assertDoesNotExist()
  }
}