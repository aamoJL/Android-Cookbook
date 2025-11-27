@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.ui_tests.ui.components.inputs

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.test_utility.ui.TestTags
import com.aamo.cookbook.ui.components.inputs.LabelledCheckBox
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LabelledCheckBox {
  @get:Rule val rule = createComposeRule()

  @Test
  fun `label visible`() {
    val label = "Label"
    rule.setContent {
      LabelledCheckBox(
        checked = false,
        onCheckedChange = { },
        label = { Text(text = label, modifier = Modifier.testTag(TestTags.NODE.name)) },
      )
    }

    rule.onNode(hasTestTag(TestTags.NODE.name), useUnmergedTree = true).assertExists()
  }

  @Test
  fun `onCheckedChange called`() {
    var called = false
    rule.setContent {
      LabelledCheckBox(
        checked = false,
        onCheckedChange = { called = true },
        label = { Text(String.EMPTY) },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).performClick()

    assert(called)
  }

  @Test
  fun `onCheckedChange called when clicking label`() {
    var called = false
    rule.setContent {
      LabelledCheckBox(
        checked = false,
        onCheckedChange = { called = true },
        label = { Text(String.EMPTY, modifier = Modifier.testTag(TestTags.NODE.name)) },
      )
    }

    rule.onNode(hasTestTag(TestTags.NODE.name), useUnmergedTree = true).performClick()

    assert(called)
  }

  @Test
  fun `checked change`() {
    var checked by mutableStateOf(false)
    rule.setContent {
      LabelledCheckBox(
        checked = checked,
        onCheckedChange = { checked = it },
        label = { Text(String.EMPTY) },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).performClick()

    assertTrue(checked)

    rule.onNodeWithTag(TestTags.NODE.name).performClick()

    assertFalse(checked)
  }
}