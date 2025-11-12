package com.aamo.cookbook.tests.ui.components.inputs

import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.R
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.FiveStarRating
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FiveStarRating {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `five stars visible`() {
    rule.activity.setContent {
      FiveStarRating(value = 2, onValueChange = {}, modifier = Modifier.testTag(TestTags.NODE.name))
    }

    val stars = rule.onNodeWithTag(TestTags.NODE.name).onChildren().fetchSemanticsNodes()

    assertEquals(5, stars.size)
  }

  @Test
  fun `init value`() {
    val value = 2
    rule.activity.setContent {
      FiveStarRating(
        value = value, onValueChange = {}, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_selected, 1)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_selected, 2)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 3)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 4)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 5)
    ).assertExists()
  }

  @Test
  fun `null value`() {
    val value: Int? = null
    rule.activity.setContent {
      FiveStarRating(
        value = value, onValueChange = {}, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 1)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 2)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 3)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 4)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 5)
    ).assertExists()
  }

  @Test
  fun `onValueChange called`() {
    var called = false
    rule.activity.setContent {
      FiveStarRating(
        value = 1,
        onValueChange = { called = true },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 3)
    ).performClick()

    assert(called)
  }

  @Test
  fun `value change`() {
    var value by mutableStateOf(1)
    rule.activity.setContent {
      FiveStarRating(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 3)
    ).performClick()

    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_selected, 1)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_selected, 2)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_selected, 3)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 4)
    ).assertExists()
    rule.onNodeWithContentDescription(
      rule.activity.getString(R.string.description_star_rating_star_icon_unselected, 5)
    ).assertExists()
  }
}