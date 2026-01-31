package com.aamo.cookbook.ui_tests.features.recipe.view.recipe_view_page.recipe_summary_screen

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
@RunWith(RobolectricTestRunner::class)
class IngredientCheckBox : PageTest() {
  val recipe = RecipeMocker.Companion.getFullMocker().apply {
    modify { it.copy(name = "Recipe") }
    chapters.first().modify { it.copy(name = "Chapter 1") }.steps.first().ingredients.first()
      .modify { it.copy(name = "Test Ingredient") }
  }.mock()

  @Before
  fun setup() = runTest {
    toRecipeViewPage(recipe)
    waitForLoading()
  }

  @Test
  fun checked() {
    rule.onNodeWithText(recipe.chapters.first().steps.first().ingredients.first().name)
      .assertIsToggleable().assertIsOff().performClick().assertIsOn()
  }
}