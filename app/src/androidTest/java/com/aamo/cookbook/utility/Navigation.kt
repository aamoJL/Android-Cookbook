package com.aamo.cookbook.utility

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.aamo.cookbook.R

class Navigation (
  private val rule:  AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity>
) {

  fun navigateTo_RecipesScreen(categoryIndex: Int) {
    rule.onAllNodesWithTag(Tags.CATEGORY_ITEM.name)[categoryIndex].performClick()
  }

  fun navigateTo_RecipeScreen(categoryIndex: Int, recipeIndex: Int) {
    navigateTo_RecipesScreen(categoryIndex)
    rule.onAllNodesWithTag(Tags.RECIPE_ITEM.name)[recipeIndex].performClick()
  }

  fun navigateTo_NewRecipeScreen() {
    rule.onNodeWithContentDescription(R.string.description_add_new_recipe).performClick()
  }
}