package com.aamo.cookbook.tests.ui.components.inputs.int_number_field

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.IntNumberField
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Component {
  @get:Rule val rule = createComposeRule()

  @Test
  fun `onValueChanged called`() {
    var onValueChangedCalled = false
    rule.setContent {
      IntNumberField(
        value = 0,
        onValueChange = { onValueChangedCalled = true },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()

    rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "1")

    assertTrue(onValueChangedCalled)
  }

  @Test
  fun `IntNumberField with enabled false`() {
    rule.setContent {
      IntNumberField(
        value = 0,
        onValueChange = {},
        enabled = false,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()

    rule.onNodeWithTag(TestTags.NODE.name).assertIsNotEnabled()
  }

  @Test
  fun `IntNumberField passing label`() {
    val labelText = "MyLabel"
    rule.setContent {
      IntNumberField(
        value = 0,
        onValueChange = {},
        label = { Text(labelText) },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()
    rule.onNodeWithText(labelText).assertExists()
  }
}