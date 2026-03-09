package com.aamo.cookbook.ui_tests.features.recipe.list.recipe_list_pages.recipes_by_bookmark_screen

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
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
class Filter : PageTest() {
  val recipes = (1..3).map {
    Recipe(name = "Recipe $it", category = "Category $it")
  }

  @Before
  fun setup() = runTest {
    toRecipesByBookmarkScreen()
    waitForLoading()

    // populate
    recipes.forEach {
      getDao().upsert(it).also { id ->
        getDao().upsert(RecipeBookmark(recipeId = id))
      }
    }

    rule.onNodeWithText(recipes.first().name).waitForDisplayed()
  }

  @Test
  fun filter() = runTest {
    recipes.forEach {
      rule.onNodeWithText(it.name).assertExists()
    }

    val filter = recipes.first().category
    rule.onNodeWithContentDescription(getString(R.string.cd_filter)).performClick()
    rule.onNodeWithText(filter).performClick()
    rule.onAllNodesWithTag(UITag.OPTION.name).assertAll(hasText(recipes.first().name))
  }
}