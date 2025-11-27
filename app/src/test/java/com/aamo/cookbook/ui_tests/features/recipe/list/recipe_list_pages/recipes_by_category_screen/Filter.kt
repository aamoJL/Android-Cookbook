package com.aamo.cookbook.ui_tests.features.recipe.list.recipe_list_pages.recipes_by_category_screen

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
class Filter : PageTest() {
  val recipes = (1..3).map {
    Recipe(name = "Recipe $it", category = "Category", subCategory = "Subcategory $it")
  }

  @Before
  fun setup() = runTest {
    toRecipesByCategoryScreen(recipes.first())
    waitForLoading()

    // populate
    recipes.drop(1).forEach {
      RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao().upsert(it)
    }

    rule.onNodeWithText(recipes.last().name).waitForDisplayed()
  }

  @Test
  fun filter() = runTest {
    recipes.forEach {
      rule.onNodeWithText(it.name, useUnmergedTree = true).assertExists()
    }

    val filter = recipes.first().subCategory
    rule.onNodeWithContentDescription(getString(R.string.cd_filter)).performClick()
    rule.onNodeWithText(filter).performClick()
    rule.onAllNodesWithTag(UITag.OPTION.name).assertAll(hasText(recipes.first().name))
  }
}