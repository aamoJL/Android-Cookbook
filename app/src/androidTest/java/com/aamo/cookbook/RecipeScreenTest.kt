package com.aamo.cookbook

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Navigation
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.assertCurrentRouteName
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.viewModel.AppViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeScreenTest {
  private lateinit var navController: TestNavHostController
  private lateinit var selectedRecipe: Recipe

  private var viewModel: AppViewModel = AppViewModel()
  private val categoryIndex = 0
  private val recipeIndex = 0

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      CookbookTheme {
        MainNavGraph(
          navController = navController,
          viewModel = viewModel
        )
      }
    }
    selectedRecipe = viewModel.getRecipes(viewModel.getCategories().elementAt(categoryIndex))
      .elementAt(recipeIndex)

    Navigation(rule).navigateTo_RecipeScreen(selectedRecipe.category, selectedRecipe.name)
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithTag(Tags.SCREEN_TITLE.name).assertTextContains(selectedRecipe.name)
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    navController.assertCurrentRouteName(Screen.Recipes.getRoute())
  }

  @Test
  fun onPagerSwipe() {
    rule.onNodeWithTag(Tags.PAGER.name)
      .performTouchInput { swipeLeft() }

    rule.onNodeWithText("1. ${selectedRecipe.chapters.elementAt(0).name}").assertExists()
  }

  @Test
  fun onProgress_incomplete() {
    val chapterCount = selectedRecipe.chapters.size

    // Swipe to next page
    rule.onNodeWithTag(Tags.PAGER.name)
      .performTouchInput { swipeLeft() }

    // Check only one checkbox
    rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name)[0].performClick()

    // Swipe to last page
    repeat(chapterCount){
      rule.onNodeWithTag(Tags.PAGER.name)
        .performTouchInput { swipeLeft() }
    }

    rule.onNodeWithText("Valmis!").assertDoesNotExist()
  }

  @Test
  fun onProgress_completed() {
    val chapterCount = selectedRecipe.chapters.size

    repeat(chapterCount){
      // Swipe to next page
      rule.onNodeWithTag(Tags.PAGER.name)
        .performTouchInput { swipeLeft() }

      // Check every progress checkbox
      rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name).apply {
        fetchSemanticsNodes().forEachIndexed { index, _ ->
          get(index).performClick()
        }
      }
    }

    // Swipe to last page
    rule.onNodeWithTag(Tags.PAGER.name)
      .performTouchInput { swipeLeft() }

    rule.onNodeWithText("Valmis!").assertExists()
  }
}