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
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.min

// TODO: other category

class CategoryScreenTest {
  private lateinit var navController: TestNavHostController
  private var viewModel: AppViewModel = AppViewModel()

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
    val categories = viewModel.getCategories()

    categories.subList(0, min(3, categories.size)).forEach {
      rule.onNodeWithText(it).assertExists()
    }
  }

  @Test
  fun onCategorySelection() {
    val index = 0
    val expectedCategory = viewModel.getCategories().elementAt(index)

    Navigation(rule).navigateTo_RecipesScreen(expectedCategory)

    navController.assertCurrentRouteName(Screen.Recipes.getRoute())
    assertEquals(expectedCategory,
      navController.currentBackStackEntry?.arguments?.getString(Screen.Recipes.argumentName))
  }

  @Test
  fun onRecipeAdd() {
    rule.onNodeWithContentDescription(R.string.description_add_new_recipe).performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeInfo.route)
    assertEquals(null,
      navController.currentBackStackEntry?.arguments?.getString(Screen.EditRecipe.argumentName)?.toUUIDorNull())
  }
}
