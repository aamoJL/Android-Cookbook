package com.aamo.cookbook

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPage
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Navigation
import com.aamo.cookbook.utility.assertCurrentRouteName
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.utility.toUUIDorNull
import com.aamo.cookbook.viewModel.AppViewModel
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.min

class RecipesScreenTest {
  private lateinit var navController: TestNavHostController
  private lateinit var selectedCategory: String

  private var viewModel: AppViewModel = AppViewModel()
  private val categoryIndex: Int = 0

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
    selectedCategory = viewModel.getCategories().elementAt(categoryIndex)

    Navigation(rule).navigateTo_RecipesScreen(selectedCategory)
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
  fun recipes_areVisible() {
    val recipes = viewModel.getRecipes(selectedCategory)

    recipes.subList(0, min(3, recipes.size)).forEach {
      rule.onNodeWithText(it.name).assertExists()
    }
  }

  @Test
  fun onRecipeSelection() {
    val index = 0
    val expectedRecipe = viewModel.getRecipes(selectedCategory).elementAt(index)

    Navigation(rule).navigateTo_RecipeScreen(expectedRecipe.category, expectedRecipe.name)

    navController.assertCurrentRouteName(Screen.Recipe.getRoute())
    TestCase.assertEquals(
      expectedRecipe.id,
      navController.currentBackStackEntry?.arguments?.getString(Screen.Recipe.argumentName)
        ?.toUUIDorNull()
    )
  }

  @Test
  fun onAddRecipe() {
    rule.onNodeWithText(R.string.description_add_new_recipe).performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeInfo.route)
    TestCase.assertEquals(
      null,
      navController.currentBackStackEntry?.arguments?.getString(Screen.Recipe.argumentName)
        ?.toUUIDorNull()
    )
  }
}