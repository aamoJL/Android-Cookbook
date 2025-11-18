@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.number_field.nullable_double_field

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
import com.aamo.cookbook.ui.components.inputs.number_field.NullableDoubleFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.Zero
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

  private var value by mutableStateOf<Double?>(Double.Zero)
  private val validator = NullableDoubleFieldValidator

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  private fun assertValue(value: Double?) {
    assertEquals(value, this.value)
  }

  private fun replaceText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).performTextReplacement(text = text)
  }

  private fun clear() {
    rule.onNodeWithTag(TestTags.NODE.name).performTextClearance()
  }

  private fun setup(onValueChange: ((Double?) -> Unit)? = null) {
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { value = it; onValueChange?.invoke(it) },
        validator = validator,
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
    assertValue(1.0)
    clear()
    assertValue(null)
    assertText(String.EMPTY)
    replaceText("1")
    assertValue(1.0)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0.0 to "0"
    rule.setContent {
      NumberField(
        value = expected.first,
        onValueChange = { fail() },
        validator = validator,
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
      "100" to ("100" to 100.0),
      "-100" to ("-100" to -100.0),
      "1.999" to ("1.999" to 1.9990),
      "-1.999" to ("-1.999" to -1.9990),
      Double.MAX_VALUE.toBigDecimal().toPlainString() to (Double.MAX_VALUE.toBigDecimal()
        .toPlainString() to Double.MAX_VALUE),
      (-Double.MAX_VALUE).toBigDecimal().toPlainString() to ((-Double.MAX_VALUE).toBigDecimal()
        .toPlainString() to -Double.MAX_VALUE),
      Double.MIN_VALUE.toBigDecimal().toPlainString() to (Double.MIN_VALUE.toBigDecimal()
        .toPlainString() to Double.MIN_VALUE),
      (-Double.MIN_VALUE).toBigDecimal().toPlainString() to ((-Double.MIN_VALUE).toBigDecimal()
        .toPlainString() to -Double.MIN_VALUE),
      "0" to ("0" to 0.0),
      String.EMPTY to (String.EMPTY to null),
      "000" to ("0" to 0.0),
      "0.0" to ("0.0" to 0.0),
      "000.000" to ("0.000" to 0.0),
      "." to ("." to null),
      "-." to ("-." to null),
      ".00" to (".00" to 0.0),
      "0.100" to ("0.100" to 0.10),
      "00100" to ("100" to 100.0),
      "0-" to ("-" to null),
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
    setup { fail(it?.toBigDecimal()?.toPlainString()) }

    val inputs = listOf(
      Double.MAX_VALUE.toBigDecimal().toPlainString() + "0000",
      (-Double.MAX_VALUE).toBigDecimal().toPlainString() + "0000",
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

    val expected = "5" to 5.0

    // sanity check
    assertThrows(AssertionError::class.java) { replaceText(expected.first) }

    inputs.forEach { input ->
      replaceText(input)
      assertText(expected.first)
      assertValue(expected.second)
    }
  }
}