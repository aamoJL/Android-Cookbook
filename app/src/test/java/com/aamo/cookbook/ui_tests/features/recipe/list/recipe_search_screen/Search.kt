package com.aamo.cookbook.ui_tests.features.recipe.list.recipe_search_screen

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
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

@RunWith(RobolectricTestRunner::class)
class Search : PageTest() {
  val recipes = (1..3).map {
    Recipe(name = it.toString())
  }

  @Before
  fun setup() = runTest {
    toRecipeSearchScreen()
    waitForLoading()

    // populate
    recipes.forEach {
      RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao().upsert(it)
    }

    rule.onNodeWithText(recipes.first().name).waitForDisplayed()
  }

  @Test
  fun filter() = runTest {
    recipes.forEach {
      rule.onNodeWithText(it.name).assertExists()
    }

    val filter = recipes.first().name
    rule.onNodeWithText(getString(R.string.ph_search)).performTextInput(filter)
    rule.onAllNodesWithTag(UITag.OPTION.name).assertAll(hasText(filter))
  }
}