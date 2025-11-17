package com.aamo.cookbook.tests.features.recipe.view.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.view.components.RecipeViewPagerIndicators
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecipeViewPagerIndicators {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `onPageChange called`() {
    val progress = listOf(false, false, false)
    var page: Int? = null

    rule.activity.setContent {
      RecipeViewPagerIndicators(
        pageIndex = 0, recipeProgress = progress, onPageChange = { page = it })
    }

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_settings_page))
      .performClick()
    Assert.assertEquals(0, page)

    rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_info_page)).performClick()
    Assert.assertEquals(1, page)

    progress.forEachIndexed { i, _ ->
      rule.onNodeWithContentDescription(rule.activity.getString(R.string.cd_chapter_x_page, i + 1))
        .performClick()
      Assert.assertEquals(i + 2, page)
    }
  }
}