package com.aamo.cookbook.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.screen.CategoriesScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CategoriesScreenTest {
  private var categories by mutableStateOf(List(5) { "category $it" })
  private var wasSelected: String? = null
  private var wasClicked: Boolean = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setContent() {
    rule.setContent {
      CookbookTheme {
        CategoriesScreen(
          categories = categories,
          onSelectCategory = { wasSelected = it },
          onAddRecipeClick = { wasClicked = true }
        )
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithText(R.string.screen_title_categories)
  }

  @Test
  fun backButton_isHidden() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertDoesNotExist()
  }

  @Test
  fun categories_areVisible() {
    val nodes = rule.onAllNodesWithTag(Tags.CATEGORY_ITEM.name).apply {
      fetchSemanticsNodes().forEachIndexed { i, _ ->
        get(i).assertTextContains(categories[i])
      }
    }

    assertEquals(categories.size, nodes.fetchSemanticsNodes().size)
  }

  @Test
  fun onCategorySelection() {
    val selectionIndex = 0

    rule.onAllNodesWithTag(Tags.CATEGORY_ITEM.name).apply {
      get(selectionIndex).performClick()
    }

    val expected = categories[selectionIndex]
    val actual = wasSelected

    assertEquals(expected, actual)
  }

  @Test
  fun onRecipeAdd() {
    rule.onNodeWithContentDescription(R.string.description_add_new_recipe).performClick()

    assert(wasClicked)
  }
}
