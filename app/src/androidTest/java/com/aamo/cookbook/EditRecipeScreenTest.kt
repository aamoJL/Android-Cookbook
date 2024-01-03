package com.aamo.cookbook

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPage
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Navigation
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.assertCurrentRouteName
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditRecipeScreenTestNewRecipe {
  private lateinit var navController: TestNavHostController

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
        )
      }
    }

    Navigation(rule).navigateTo_EditRecipeScreen()
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
      .assertTextContains(rule.activity.getString(R.string.screen_title_new_recipe))
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack_noChanges() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    navController.assertCurrentRouteName(Screen.Categories.getRoute())
  }

  @Test
  fun onBack_withChanges() {
    rule.onNodeWithText(R.string.textfield_recipe_name).performTextInput("uusi resepti")

    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeInfo.route)
  }

  @Test
  fun onAddNewChapter() {
    rule.onNodeWithContentDescription(R.string.description_form_add_new_item).performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeChapter.route)
  }

  @Test
  fun onEditChapter() {
    rule.onAllNodesWithTag(Tags.CHAPTER_ITEM.name)[0].performClick()

    navController.assertCurrentRouteName(EditRecipeScreenPage.EditRecipeChapter.route)
  }
}

class EditRecipeChapterScreenTestNewChapter {
  private lateinit var navController: TestNavHostController

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
        )
      }
    }

    Navigation(rule).navigateTo_EditRecipeScreen()

    // Change page to new chapter page
    rule.onNodeWithContentDescription(R.string.description_form_add_new_item).performClick()
  }

  @Test
  fun pageTitle_equals() {
    rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
      .assertTextContains(rule.activity.getString(R.string.screen_title_new_chapter))
  }

  @Test
  fun formTitle_equals() {
    // TODO: update when the default chapter has been removed
    rule.onNodeWithText(rule.activity.getString(R.string.form_title_chapter, 2))
  }
}