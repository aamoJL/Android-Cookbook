package com.aamo.cookbook.tests.ui.components

import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.utility.tags.UITag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@Suppress("HardCodedStringLiteral")
@RunWith(RobolectricTestRunner::class)
class PrimaryTopAppBar {
  @get:Rule val rule = createComposeRule()

  @Test
  fun title_visible() {
    val title = "Title"

    rule.apply {
      setContent {
        PrimaryTopAppBar(title = title)
      }

      onNodeWithTag(UITag.SCREEN_TITLE.name).assertExists()
    }
  }

  @Test
  fun `back button not visible when not set`() {
    val title = "Title"

    rule.apply {
      setContent {
        PrimaryTopAppBar(title = title, onBack = null)
      }

      onNodeWithTag(UITag.BACK_BUTTON.name).assertDoesNotExist()
    }
  }

  @Test
  fun `back button visible when set`() {
    val title = "Title"

    rule.apply {
      setContent {
        PrimaryTopAppBar(title = title, onBack = {})
      }

      onNodeWithTag(UITag.BACK_BUTTON.name).assertExists()
    }
  }

  @Test
  fun `actions visible when set`() {
    val title = "Title"

    rule.apply {
      setContent {
        PrimaryTopAppBar(title = title, actions = {
          Button(onClick = {}, modifier = Modifier.testTag(TestTags.VISIBLE.name)) {}
        })
      }

      onNodeWithTag(TestTags.VISIBLE.name).assertExists()
    }
  }
}