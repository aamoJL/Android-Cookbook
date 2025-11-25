package com.aamo.cookbook.ui_tests.features.recipe.list.recipe_list_pages.recipes_by_category_screen

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
class Navigation : PageTest() {
  val recipe = Recipe(name = "Recipe", category = "Category")

  @Before
  fun setup() = runTest {
    toRecipesByCategoryScreen(recipe)
    waitForLoading()
  }

  @Test
  fun `on recipesByCategoryScreen`() = runTest {
    rule.onNodeWithText(recipe.category).assertExists()
  }

  @Test
  fun `to back`() = runTest {
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClick()
    rule.onNodeWithText(getString(R.string.app_name)).assertExists()
  }

  @Test
  fun `to recipeViewPage`() = runTest {
    rule.onNodeWithText(recipe.name).waitForDisplayed().also {
      it.performClick()
    }

    waitForLoading()

    rule.onNodeWithText(getString(R.string.title_ingredients)).assertExists()
  }

  @Test
  fun `to recipeSearchScreen`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_search)).performClick()
    rule.onNodeWithText(getString(R.string.ph_search)).assertExists()
  }

  @Test
  fun `to recipeFormPage`() = runTest {
    rule.onNodeWithContentDescription(getString(R.string.cd_add_new_recipe)).performClick()
    rule.onNodeWithText(getString(R.string.screen_title_new_recipe)).assertExists()
  }
}