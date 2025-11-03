package com.aamo.cookbook.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {
  private var recipes by mutableStateOf(List(5) {
    RecipeWithBookmarkAndRating(
      recipe = Recipe(id = it, name = "recipe $it", category = "category", servings = it),
      bookmark = null,
      rating = null
    )
  })
  private var wasSelected: Recipe? = null
  private var wasClicked: Boolean = false

  @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {
      CookbookTheme {
        RecipesScreen(
          title = recipes.first().recipe.category,
          recipes = recipes,
          onSelectRecipe = { wasSelected = it },
          onBack = { wasClicked = true },
          onSearch = { wasClicked = true },
          onAdd = { wasClicked = true })
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithText(recipes.first().recipe.category).assertExists()
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.cd_navigate_back).assertExists()
  }

  @Test
  fun onBack() {
    rule.onNodeWithContentDescription(R.string.cd_navigate_back).performClick()

    assert(wasClicked)
  }

  @Test
  fun recipes_areVisible() {
    val nodes = rule.onAllNodesWithTag(UITag.RECIPE_ITEM.name).apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        get(i).assertTextContains(recipes[i].recipe.name)
      }
    }

    assertEquals(recipes.size, nodes.fetchSemanticsNodes().size)
  }

  @Test
  fun onRecipeSelection() = runTest {
    val selectionIndex = 0

    rule.onAllNodesWithTag(UITag.RECIPE_ITEM.name).apply {
      get(selectionIndex).performClick()
    }

    val expected = recipes[selectionIndex].recipe
    val actual = wasSelected

    assertEquals(expected, actual)
  }
}