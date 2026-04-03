package com.aamo.cookbook.ui_tests.features.recipe.form.recipe_form_page.new_recipe

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.ui.rules.PageTest
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
  @Before
  fun setup() = runTest {
    toRecipeFormPage()
    waitForLoading()
  }

  @Test
  fun `on recipeFormInfoScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.screen_title_new_recipe)).assertExists()
  }

  @Test
  fun `to back`() = runTest {
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.app_name)).assertExists()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `to recipeFormChapterScreen`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_add_new_chapter)).performClick()
    rule.onNodeWithText(getString(R.string.title_chapter_information, "1")).assertExists()
  }
}