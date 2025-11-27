package com.aamo.cookbook.ui_tests.features.recipe.view.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.view.components.RecipeViewTopBar
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecipeViewTopBar {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `onBack called`() {
    var called = false
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onOpenCalculator = {},
        onOpenTimer = {},
        onBack = { called = true })
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_navigate_back))
      .performClick()

    assertTrue(called)
  }

  @Test
  fun `onOpenCalculator called`() {
    var called = false
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onOpenCalculator = { called = true },
        onOpenTimer = {},
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_open_calculator))
      .performClick()

    assertTrue(called)
  }

  @Test
  fun `onOpenTimer called`() {
    var called = false
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = {},
        onOpenCalculator = {},
        onOpenTimer = { called = true },
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_open_timer))
      .performClick()

    assertTrue(called)
  }

  @Test
  fun `onEdit called`() {
    var called = false
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = { called = true },
        onCopy = {},
        onUpdateBookmark = {},
        onOpenCalculator = {},
        onOpenTimer = { },
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_edit_recipe))
      .assertDoesNotExist()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_more_options))
      .performClick()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_edit_recipe))
      .performClick()

    assertTrue(called)
  }

  @Test
  fun `onCopy called`() {
    var called = false
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = {},
        onCopy = { called = true },
        onUpdateBookmark = {},
        onOpenCalculator = {},
        onOpenTimer = { },
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_copy_recipe))
      .assertDoesNotExist()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_more_options))
      .performClick()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_copy_recipe))
      .performClick()

    assertTrue(called)
  }

  @Test
  fun `onBookmark called when is not bookmarked`() {
    var value: Boolean? = null
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = false,
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = { value = it },
        onOpenCalculator = {},
        onOpenTimer = { },
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_add_bookmark))
      .assertDoesNotExist()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_more_options))
      .performClick()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_add_bookmark))
      .performClick()

    assertEquals(true, value)
  }

  @Test
  fun `onBookmark called when bookmarked`() {
    var value: Boolean? = null
    rule.activity.setContent {
      RecipeViewTopBar(
        title = String.EMPTY,
        isBookmarked = true,
        onEdit = {},
        onCopy = {},
        onUpdateBookmark = { value = it },
        onOpenCalculator = {},
        onOpenTimer = { },
        onBack = {})
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_remove_bookmark))
      .assertDoesNotExist()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_more_options))
      .performClick()

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.btn_remove_bookmark))
      .performClick()

    assertEquals(false, value)
  }
}