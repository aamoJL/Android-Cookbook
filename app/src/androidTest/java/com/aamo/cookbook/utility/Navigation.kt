package com.aamo.cookbook.utility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.aamo.cookbook.R

class Navigation (
  private val rule:  AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>
) {

  fun navigateTo_RecipesScreen(categoryName: String) {
    rule.onNodeWithText(categoryName).performClick()
  }

  fun navigateTo_RecipeScreen(category: String, recipeName: String) {
    navigateTo_RecipesScreen(category)
    rule.onNodeWithText(recipeName).performClick()
  }

  fun navigateTo_EditRecipeScreen() {
    rule.onNodeWithContentDescription(R.string.description_add_new_recipe).performClick()
  }
}