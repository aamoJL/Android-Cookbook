package com.aamo.cookbook.tests.ui.components.inputs.float_number_field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.FloatNumberField
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
    val expected = 0f to "0"
    rule.setContent {
      FloatNumberField(
        value = expected.first,
        onValueChange = { fail() },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    assertText(expected.second)
  }

  @Test
  fun `text on external value change`() {
    val expected = 10f to "10"
    var value by mutableFloatStateOf(5f)
    rule.setContent {
      FloatNumberField(
        value = value, onValueChange = { fail() }, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    value = expected.first
    assertText(expected.second)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0f to "0"
    var value by mutableStateOf(5f)
    rule.setContent {
      FloatNumberField(
        value = value,
        onValueChange = { fail() },
        readOnly = true,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    assertText("5")

    value = expected.first

    assertText(expected.second)
    assertEquals(expected.first, value)
  }

  @Test
  fun `readonly when value is not finite`() {
    var value by mutableStateOf(0f)
    rule.setContent {
      FloatNumberField(
        value = value,
        onValueChange = { value = it },
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")

    value = Float.NaN

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }

    value = Float.POSITIVE_INFINITY

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }

    value = Float.NEGATIVE_INFINITY

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }
  }

  @Test
  fun `change valid value`() {
    var value by mutableStateOf(0f)

    rule.setContent {
      FloatNumberField(
        value = value, onValueChange = { fail() }, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    val inputOutputs = listOf(
      (1f to "1"),
      (-1f to "-1"),
      (1.99999f to "1.99999"),
      (-1.99999f to "-1.99999"),
      (Float.MAX_VALUE to "340282350000000000000000000000000000000"),
      (-Float.MAX_VALUE to "-340282350000000000000000000000000000000"),
      (Float.MIN_VALUE to "0.0000000000000000000000000000000000000000000014"),
      (-Float.MIN_VALUE to "-0.0000000000000000000000000000000000000000000014"),
      (0f to "0"),
    )

    inputOutputs.forEach { (input, output) ->
      value = input
      assertText(output)
    }
  }

  @Test
  fun `change invalid value`() {
    var value by mutableStateOf(0f)
    rule.setContent {
      FloatNumberField(
        value = value, onValueChange = { fail() }, modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    val inputOutputs = listOf(
      (Float.POSITIVE_INFINITY to Float.POSITIVE_INFINITY.toString()),
      (Float.NEGATIVE_INFINITY to Float.NEGATIVE_INFINITY.toString()),
      (Float.NaN to Float.NaN.toString()),
    )

    inputOutputs.forEach { (input, output) ->
      value = input
      assertText(output)
    }
  }
}