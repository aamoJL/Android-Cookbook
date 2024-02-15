package com.aamo.cookbook.inputs

import androidx.activity.ComponentActivity
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import com.aamo.cookbook.ui.components.BasicDismissibleItem
import com.aamo.cookbook.ui.theme.CookbookTheme
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BasicDismissibleItemTest {
  private val tag = "tag"
  private val animationDuration = 500
  private var wasDismissed = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        BasicDismissibleItem(
          dismissAction = { true.also { wasDismissed = true } },
          animationDuration = animationDuration
        ){
          ListItem(
            modifier = Modifier.testTag(tag),
            headlineContent = {
            Text(text = "test item")
          })
        }
      }
    }
  }

  @Test
  fun swipe_Left() = runTest {
    getInput().performTouchInput { swipeLeft() }

    rule.mainClock.advanceTimeBy(animationDuration.toLong())

    assertFalse(wasDismissed)
  }

  @Test
  fun swipe_Right() = runTest {
    getInput().performTouchInput { swipeRight() }

    rule.mainClock.advanceTimeBy(animationDuration.toLong())

    assert(wasDismissed)
  }

  private fun getInput() = rule.onNodeWithTag(tag)
}