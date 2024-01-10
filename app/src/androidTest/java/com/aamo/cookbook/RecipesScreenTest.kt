package com.aamo.cookbook

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPage
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Navigation
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.assertCurrentRouteName
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipesScreenTest {
  private lateinit var navController: TestNavHostController
  private lateinit var selectedCategory: String

  private lateinit var viewModel: AppViewModel
  private val categoryIndex: Int = 0

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {
      viewModel = viewModel(factory = ViewModelProvider.Factory)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      CookbookTheme {
        MainNavGraph(
          navController = navController,
          viewModel = viewModel
        )
      }
    }

    Navigation(rule).navigateTo_RecipesScreen(0)
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithText(selectedCategory).assertExists()
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    navController.assertCurrentRouteName(Screen.Categories.getRoute())
  }

  @Test
  fun recipes_areVisible() = runTest {
    val firstRecipe = viewModel.getRecipesByCategory(selectedCategory).first().first()
    rule.onNodeWithTag(Tags.RECIPE_ITEM.name).assertTextContains(firstRecipe.name)
  }

  @Test
  fun onRecipeSelection() = runTest {
    val index = 0
    val expectedRecipe = viewModel.getRecipesByCategory(selectedCategory).first().elementAt(index)

    Navigation(rule).navigateTo_RecipeScreen(0,0)

    navController.assertCurrentRouteName(Screen.Recipe.getRoute())
    TestCase.assertEquals(
      expectedRecipe.id,
      navController.currentBackStackEntry?.arguments?.getInt(Screen.Recipe.argumentName, -1)
    )
  }

  @Test
  fun onAddRecipe() {
    rule.onNodeWithText(R.string.description_add_new_recipe).performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeInfo.route)
    TestCase.assertEquals(
      -1,
      navController.currentBackStackEntry?.arguments?.getInt(Screen.Recipe.argumentName, -1)
    )
  }
}