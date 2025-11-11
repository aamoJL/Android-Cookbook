@file:Suppress("HardCodedStringLiteral")

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
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.IntNumberField
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

  private var value by mutableStateOf(0)

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  private fun assertValue(value: Int) {
    assertEquals(value, this.value)
  }

  private fun inputText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = text)
  }

  private fun clear() {
    rule.onNodeWithTag(TestTags.NODE.name).performTextClearance()
  }

  private fun setup(onValueChange: (Int) -> Unit = { value = it }) {
    rule.setContent {
      IntNumberField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
  }

  @Test
  fun `setup sanity`() {
    var onValueChangeCalled = false
    setup(onValueChange = { onValueChangeCalled = true })
    inputText("1")
    assertTrue(onValueChangeCalled)
  }

  @Test
  fun `clear sanity`() {
    setup()
    inputText("1")
    assertValue(1)
    clear()
    assertValue(0)
    assertText("0")
    inputText("1")
    assertValue(1)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0 to "0"
    rule.setContent {
      IntNumberField(
        value = expected.first,
        onValueChange = { fail() },
        readOnly = true,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }
    rule.waitForIdle()
    assertText(expected.second)
    assertThrows(AssertionError::class.java) {
      inputText("5")
    }
    rule.waitForIdle()
    assertText(expected.second)
    assertEquals(expected.first, value)
  }

  @Test
  fun `input empty`() {
    setup()
    (String.EMPTY to ("0" to 0)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second)
    }
  }

  @Test
  fun `input same`() {
    setup { fail() }
    inputText(value.toString())
  }

  @Test
  fun `input valid text`() {
    setup()
    // [input] to ([text] to [value])
    ("100" to ("100" to 100)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
    ("-100" to ("-100" to -100)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
    ("2147483647" to ("2147483647" to Int.MAX_VALUE)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
    ("-2147483647" to ("-2147483647" to -Int.MAX_VALUE)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
    ("-2147483648" to ("-2147483648" to Int.MIN_VALUE)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
    ("00100" to ("100" to 100)).also { (input, output) ->
      inputText(input); assertText(output.first); assertValue(output.second); clear()
    }
  }

  @Test
  fun `input invalid text`() {
    setup { fail() }
    "1.9999".also { inputText(it); assertText("0"); assertValue(0) }
    "-1.999".also { inputText(it); assertText("0"); assertValue(0) }
    "0.0".also { inputText(it); assertText("0"); assertValue(0) }
    "000.000".also { inputText(it); assertText("0"); assertValue(0) }
    ".".also { inputText(it); assertText("0"); assertValue(0) }
    ".00".also { inputText(it); assertText("0"); assertValue(0) }
    ".00".also { inputText(it); assertText("0"); assertValue(0) }
    "0.100".also { inputText(it); assertText("0"); assertValue(0) }
    "9990282350000000000000000000000000000000".also {
      inputText(it); assertText("0"); assertValue(0)
    }
    "-9990282350000000000000000000000000000000".also {
      inputText(it); assertText("0"); assertValue(0)
    }
    "..".also { inputText(it); assertText("0"); assertValue(0) }
    ".-1".also { inputText(it); assertText("0"); assertValue(0) }
    "1.0.0".also { inputText(it); assertText("0"); assertValue(0) }
    "1 2".also { inputText(it); assertText("0"); assertValue(0) }
    " ".also { inputText(it); assertText("0"); assertValue(0) }
    "test".also { inputText(it); assertText("0"); assertValue(0) }
    "12f".also { inputText(it); assertText("0"); assertValue(0) }
    "--12".also { inputText(it); assertText("0"); assertValue(0) }
    "-.-12".also { inputText(it); assertText("0"); assertValue(0) }
    "1-2".also { inputText(it); assertText("0"); assertValue(0) }
    "0".also { inputText(it); assertText("0"); assertValue(0) }
    "000".also { inputText(it); assertText("0"); assertValue(0) }
  }
}