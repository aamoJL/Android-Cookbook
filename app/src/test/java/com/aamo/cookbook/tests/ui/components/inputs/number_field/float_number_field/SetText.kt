@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.number_field.float_number_field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.number_field.FloatFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SetText {
  @get:Rule val rule = createComposeRule()

  private var value by mutableStateOf(0f)

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  private fun assertValue(value: Float) {
    assertEquals(value, this.value)
  }

  private fun replaceText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).performTextReplacement(text = text)
  }

  private fun clear() {
    rule.onNodeWithTag(TestTags.NODE.name).performTextClearance()
  }

  private fun setup(onValueChange: ((Float) -> Unit)? = null) {
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { value = it; onValueChange?.invoke(it) },
        validator = FloatFieldValidator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
  }

  @Test
  fun `setup sanity`() {
    var onValueChangeCalled = false
    setup(onValueChange = { onValueChangeCalled = true })
    replaceText("1")
    assertTrue(onValueChangeCalled)
  }

  @Test
  fun `clear sanity`() {
    setup()
    replaceText("1")
    assertValue(1f)
    clear()
    assertValue(0f)
    assertText("0")
    replaceText("1")
    assertValue(1f)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0f to "0"
    rule.setContent {
      NumberField(
        value = expected.first,
        onValueChange = { fail() },
        validator = FloatFieldValidator,
        readOnly = true,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()
    assertText(expected.second)
    assertThrows(AssertionError::class.java) {
      replaceText("5")
    }
    rule.waitForIdle()
    assertText(expected.second)
    assertEquals(expected.first, value)
  }

  @Test
  fun `input same`() {
    setup { fail() }
    replaceText(value.toString())
  }

  @Test
  fun `input valid text`() {
    setup()

    val inputOutputs = listOf(
      "100" to ("100" to 100f),
      "-100" to ("-100" to -100f),
      "1.999" to ("1.999" to 1.999f),
      "-1.999" to ("-1.999" to -1.999f),
      "340282350000000000000000000000000000000" to ("340282350000000000000000000000000000000" to Float.MAX_VALUE),
      "-340282350000000000000000000000000000000" to ("-340282350000000000000000000000000000000" to -Float.MAX_VALUE),
      "0.0000000000000000000000000000000000000000000014" to ("0.0000000000000000000000000000000000000000000014" to Float.MIN_VALUE),
      "-0.0000000000000000000000000000000000000000000014" to ("-0.0000000000000000000000000000000000000000000014" to -Float.MIN_VALUE),
      "0" to ("0" to 0f),
      String.EMPTY to ("0" to 0f),
      "000" to ("0" to 0f),
      "0.0" to ("0.0" to 0f),
      "000.000" to ("0.000" to 0f),
      "." to ("." to 0f),
      "-." to ("-." to 0f),
      ".00" to (".00" to 0f),
      "0.100" to ("0.100" to 0.1f),
      "00100" to ("100" to 100f),
      "0-" to ("-" to 0f),
    )

    inputOutputs.forEach { (input, output) ->
      replaceText(input)
      assertText(output.first)
      assertValue(output.second)
      clear()
    }
  }

  @Test
  fun `input invalid text`() {
    setup { fail() }

    val inputs = listOf(
      "9990282350000000000000000000000000000000",
      "-9990282350000000000000000000000000000000",
      "..",
      ".-1",
      "1.0.0",
      "1 2",
      " ",
      "..",
      "test",
      "12f",
      "--12",
      "-.-12",
      "1-2",
    )

    val expected = "5" to 5f

    // sanity check
    assertThrows(AssertionError::class.java) { replaceText(expected.first) }

    inputs.forEach { input ->
      replaceText(input)
      assertText(expected.first)
      assertValue(expected.second)
    }
  }
}