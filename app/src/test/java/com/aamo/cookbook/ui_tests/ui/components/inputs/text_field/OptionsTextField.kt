@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.ui_tests.ui.components.inputs.text_field

import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.MainActivity
import com.aamo.cookbook.test_utility.ui.TestTags
import com.aamo.cookbook.ui.components.inputs.text_field.OptionsTextField
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.tags.UITag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OptionsTextField {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `value change`() {
    var value by mutableStateOf("Value")
    rule.activity.setContent {
      OptionsTextField(value = value, onValueChange = {}, options = emptyList())
    }

    rule.onNode(hasText(value)).assertExists()

    value = "New Value"

    rule.onNode(hasText(value)).assertExists()
  }

  @Test
  fun `onValueChange called`() {
    var called = false
    rule.activity.setContent {
      OptionsTextField(
        value = String.EMPTY,
        onValueChange = { called = true },
        options = emptyList(),
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).onChildAt(0).performTextInput("Value")

    assert(called)
  }

  @Test
  fun `options visibility`() {
    var options: List<String> by mutableStateOf(emptyList())
    rule.activity.setContent {
      OptionsTextField(
        value = String.EMPTY,
        onValueChange = { },
        options = options,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).onChildAt(0).performClick()
    assertTrue(rule.onAllNodesWithTag(UITag.OPTION.name).fetchSemanticsNodes().isEmpty())

    options = listOf("1", "2", "3")

    rule.onNodeWithTag(TestTags.NODE.name).onChildAt(0).performClick()
    assertEquals(options.size, rule.onAllNodesWithTag(UITag.OPTION.name).fetchSemanticsNodes().size)
  }

  @Test
  fun `option click`() {
    val option = "Option 1"
    var value by mutableStateOf(String.EMPTY)
    rule.activity.setContent {
      OptionsTextField(
        value = String.EMPTY,
        onValueChange = { value = it },
        options = listOf(option),
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).onChildAt(0).performClick()
    rule.onNodeWithTag(UITag.OPTION.name).performClick()

    assertEquals(option, value)
  }
}