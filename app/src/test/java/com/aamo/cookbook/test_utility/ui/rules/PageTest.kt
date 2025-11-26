package com.aamo.cookbook.test_utility.ui.rules

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import org.junit.After
import org.junit.Rule

open class PageTest {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @After
  open fun cleanup() {
    RecipeDatabase.getDatabase(rule.activity.applicationContext).clearAllTables()
  }

  fun getDao(): RecipeDao {
    return RecipeDatabase.getDatabase(rule.activity).recipeDao()
  }

  fun getString(@StringRes id: Int): String {
    return rule.activity.getString(id)
  }

  fun getString(@StringRes id: Int, param: String): String {
    return rule.activity.getString(id, param)
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
    getDao().upsert(recipe)

    waitForLoading()
    rule.onNodeWithText(recipe.category).waitForDisplayed().performClick()
  }

  suspend fun toRecipeFormPage() {
    waitForLoading()
    rule.onNodeWithText(getString(R.string.btn_new)).performClick()
  }

  suspend fun toRecipeFormPage(recipe: RecipeWithChaptersStepsAndIngredients) {
    toRecipeViewPage(recipe)
    waitForLoading()
    rule.onNodeWithContentDescription(getString(R.string.cd_more_options)).performClick()
    rule.onNodeWithContentDescription(getString(R.string.btn_edit_recipe)).performClick()
  }

  suspend fun toRecipeViewPage(recipe: RecipeWithChaptersStepsAndIngredients): RecipeWithChaptersStepsAndIngredients {
    val recipe = getDao().let {
      it.getCompleteRecipe(it.upsert(recipe))
    } ?: throw Error()

    toRecipeSearchScreen()
    waitForLoading()
    rule.onNodeWithText(recipe.recipe.name).performClickWithKeyboard()

    return recipe
  }
}