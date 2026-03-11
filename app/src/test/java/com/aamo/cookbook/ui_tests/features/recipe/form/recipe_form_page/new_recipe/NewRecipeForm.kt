package com.aamo.cookbook.ui_tests.features.recipe.form.recipe_form_page.new_recipe

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.aamo.cookbook.R
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
class NewRecipeForm : PageTest() {
  @Before
  fun setup() = runTest {
    toRecipeFormPage()
    waitForLoading()
  }

  @Test
  fun submit() = runTest {
    // Info
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Recipe")
    rule.onNodeWithText(getString(R.string.label_servings)).performTextReplacement("3")
    rule.onNodeWithText(getString(R.string.label_category)).performTextInput("Category")

    // Chapter
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Chapter")

    // Step
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_description)).performTextInput("Step")

    // Ingredient
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Ingredient")

    // Save
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Step
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Chapter
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Recipe

    // Submit
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsEnabled()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick()

    // on RecipeViewPage
    rule.onNodeWithText(getString(R.string.title_ingredients)).waitForDisplayed().assertExists()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name).assert(hasText("Recipe"))
  }

  @Test
  fun submit_correct_backstack() = runTest {
    // Info
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Recipe")
    rule.onNodeWithText(getString(R.string.label_servings)).performTextReplacement("3")
    rule.onNodeWithText(getString(R.string.label_category)).performTextInput("Category")

    // Chapter
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Chapter")

    // Step
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_description)).performTextInput("Step")

    // Ingredient
    rule.onNodeWithContentDescription(getString(R.string.cd_form_add_new_item)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsNotEnabled()
    rule.onNodeWithText(getString(R.string.label_name)).performTextInput("Ingredient")

    // Save
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Step
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Chapter
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick() // to Recipe

    // Submit
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).assertIsEnabled()
    rule.onNodeWithContentDescription(getString(R.string.cd_save)).performClick()

    // on RecipeViewPage
    rule.onNodeWithText(getString(R.string.title_ingredients)).waitForDisplayed().assertExists()
    rule.onNodeWithTag(UITag.PAGE_TITLE.name).assert(hasText("Recipe"))

    // back
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_new_recipe)).assertDoesNotExist()
    rule.onNodeWithText(getString(R.string.screen_title_edit_recipe)).assertDoesNotExist()
  }

  @Test
  fun `delete button hidden`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_delete_recipe)).assertDoesNotExist()
  }
}