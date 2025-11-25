package com.aamo.cookbook.ui_tests.features.recipe.list.recipe_list_pages.recipe_search_screen

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.performClickWithKeyboard
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Navigation : PageTest() {
  @Before
  fun setup() = runTest {
    toRecipeSearchScreen()
    waitForLoading()
  }

  @Test
  fun `on recipeSearchScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.ph_search)).assertExists()
  }

  @Test
  fun `to back`() = runTest {
    rule.onNodeWithTag(UITag.BACK_BUTTON.name, useUnmergedTree = true).performClickWithKeyboard()
    rule.onNodeWithText(getString(R.string.app_name)).assertExists()
  }

  @Test
  fun `to recipeViewPage`() = runTest {
    val recipeName = "123"
    RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao()
      .upsert(Recipe(name = recipeName))

    rule.onNodeWithText(recipeName).waitForDisplayed().also {
      it.performClickWithKeyboard()
    }

    waitForLoading()

    rule.onNodeWithText(getString(R.string.title_ingredients)).assertExists()
  }
}