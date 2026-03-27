package com.aamo.cookbook.ui_tests.features.recipe.form.recipe_form_page.recipe_chapter_screen

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
class StepNoteField : PageTest() {
  @Before
  fun setup() = runTest {
    toRecipeFormPage()
    waitForLoading()
    rule.onNodeWithContentDescription(getString(R.string.cd_add_new_chapter)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.cd_add_step)).performClick()
  }

  @Test
  fun `note field toggle`() = runTest {
    rule.onNodeWithText(getString(R.string.label_note).asOptionalLabel()).isNotDisplayed()
    rule.onAllNodesWithContentDescription(getString(R.string.cd_toggle_note_field)).onLast()
      .performClick()
    rule.onNodeWithText(getString(R.string.label_note).asOptionalLabel()).isDisplayed()
  }
}