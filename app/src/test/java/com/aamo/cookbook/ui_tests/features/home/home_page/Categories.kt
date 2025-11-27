package com.aamo.cookbook.ui_tests.features.home.home_page

import androidx.compose.ui.test.onNodeWithText
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
class Categories : PageTest() {
  @Before
  fun setup() = runTest {
    waitForLoading()
  }

  @Test
  fun `categories visibility`() = runTest {
    val category = "123"

    rule.onNodeWithText(category).assertDoesNotExist()

    RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao()
      .upsert(Recipe(category = "123"))

    rule.onNodeWithText(category).waitForDisplayed()
    rule.onNodeWithText(category).assertExists()
  }
}