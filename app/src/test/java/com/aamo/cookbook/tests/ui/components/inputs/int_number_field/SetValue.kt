package com.aamo.cookbook.tests.ui.components.inputs.int_number_field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.IntNumberField
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SetValue {
  @get:Rule val rule = createComposeRule()

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  @Test
  fun `initial render with value`() {
    val expected = 0 to "0"
    rule.setContent {
      IntNumberField(
        value = expected.first,
        onValueChange = { fail() },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()

    assertText(expected.second)
  }

  @Test
  fun `text on external value change`() {
    val expected = 10 to "10"
    var value by mutableStateOf(5)
    rule.setContent {
      IntNumberField(
        value = value, onValueChange = { fail() }, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    value = expected.first
    rule.waitForIdle()

    assertText(expected.second)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0 to "0"
    var value by mutableStateOf(5)
    rule.setContent {
      IntNumberField(
        value = value,
        onValueChange = { fail() },
        readOnly = true,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()

    assertText("5")

    value = expected.first

    rule.waitForIdle()

    assertText(expected.second)
    assertEquals(expected.first, value)
  }

  @Test
  fun `change value`() {
    var value by mutableStateOf(0)

    rule.setContent {
      IntNumberField(
        value = value, onValueChange = { fail() }, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    (1 to "1").also { input -> value = input.first }.also { input -> assertText(input.second) }
    (-1 to "-1").also { input -> value = input.first }.also { input -> assertText(input.second) }
    (Int.MAX_VALUE to "2147483647").also { input -> value = input.first }
      .also { input -> assertText(input.second) }
    (-Int.MAX_VALUE to "-2147483647").also { input -> value = input.first }
      .also { input -> assertText(input.second) }
    (Int.MIN_VALUE to "-2147483648").also { input -> value = input.first }
      .also { input -> assertText(input.second) }
    (0 to "0").also { input -> value = input.first }.also { input -> assertText(input.second) }
  }
}