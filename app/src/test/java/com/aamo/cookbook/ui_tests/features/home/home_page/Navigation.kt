package com.aamo.cookbook.ui_tests.features.home.home_page

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Navigation : PageTest() {
  @Before
  fun setup() = runTest {
    waitForLoading()
  }

  @Test
  fun `on homeScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.app_name)).assertExists()
  }

  @Test
  fun `to recipeSearchScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.btn_search)).performClick()
    waitForLoading()
    rule.onNodeWithText(getString(R.string.ph_search)).assertExists()
  }

  @Test
  fun `to recipesByBookmarkScreen`() = runTest {
    rule.onNodeWithText(getString(R.string.btn_bookmarks)).performClick()
    waitForLoading()
    rule.onNodeWithText(getString(R.string.screen_title_bookmarks)).assertExists()
  }

  @Test
  fun `to recipeFormPage`() = runTest {
    rule.onNodeWithText(getString(R.string.btn_new)).performClick()
    waitForLoading()
    rule.onNodeWithText(getString(R.string.screen_title_new_recipe)).assertExists()
  }

  @Test
  fun `to recipesByCategoryScreen`() = runTest {
    val category = "123"

    RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao()
      .upsert(Recipe(category = "123"))

    rule.onNodeWithText(category).waitForDisplayed().also {
      it.performClick()
    }

    waitForLoading()
    rule.onNodeWithText(category).assertExists()
  }
}