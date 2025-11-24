package com.aamo.cookbook.test_utility.ui.rules

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import org.junit.After
import org.junit.Rule

open class PageTest {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @After
  open fun cleanup() {
    RecipeDatabase.getDatabase(rule.activity.applicationContext).clearAllTables()
  }

  fun getString(@StringRes id: Int): String {
    return rule.activity.getString(id)
  }

  suspend fun toRecipeSearchScreen() {
    waitForLoading()
    rule.onNodeWithText(getString(R.string.btn_search)).performClick()
  }

  suspend fun toRecipesByBookmarkScreen() {
    waitForLoading()
    rule.onNodeWithText(getString(R.string.btn_bookmarks)).performClick()
  }

  suspend fun toRecipesByCategoryScreen(recipe: Recipe) {
    RecipeDatabase.getDatabase(rule.activity.applicationContext).recipeDao().upsert(recipe)
    
    rule.onNodeWithText(recipe.category).waitForDisplayed().performClick()
  }
}