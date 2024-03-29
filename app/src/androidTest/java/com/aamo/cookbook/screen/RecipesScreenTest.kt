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
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {
  private var recipes by mutableStateOf(List(5) {
    RecipeWithFavoriteAndRating(
      value = Recipe(id = it, name = "recipe $it", category = "category", servings = it),
      favorite = null,
      rating = null
    )
  })
  private var wasSelected: Recipe? = null
  private var wasClicked: Boolean = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {
      CookbookTheme {
        RecipesScreen(
          title = recipes.first().value.category,
          recipes = recipes,
          onSelectRecipe = {wasSelected = it},
          onBack = { wasClicked = true },
          onSearch = { wasClicked = true },
          onAdd = { wasClicked = true }
        )
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithText(recipes.first().value.category).assertExists()
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    assert(wasClicked)
  }

  @Test
  fun recipes_areVisible() {
    val nodes = rule.onAllNodesWithTag(Tags.RECIPE_ITEM.name).apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        get(i).assertTextContains(recipes[i].value.name)
      }
    }

    assertEquals(recipes.size, nodes.fetchSemanticsNodes().size)
  }

  @Test
  fun onRecipeSelection() = runTest {
    val selectionIndex = 0

    rule.onAllNodesWithTag(Tags.RECIPE_ITEM.name).apply {
      get(selectionIndex).performClick()
    }

    val expected = recipes[selectionIndex].value
    val actual = wasSelected

    assertEquals(expected, actual)
  }
}