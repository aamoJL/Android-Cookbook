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
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.test_utility.TestTags
import com.aamo.cookbook.ui.components.inputs.number_field.NullableDoubleFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.Zero
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

  val validator = NullableDoubleFieldValidator

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  @Test
  fun `initial render with value`() {
    val expected = Double.Zero to "0"
    rule.setContent {
      NumberField(
        value = expected.first,
        onValueChange = { fail() },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    assertText(expected.second)
  }

  @Test
  fun `text on external value change`() {
    val expected = 10.0 to "10"
    var value by mutableStateOf(5.0)
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { fail() },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    value = expected.first
    assertText(expected.second)
  }

  @Test
  fun `readOnly true`() {
    val expected = 0.0 to "0"
    var value by mutableStateOf(5.0)
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { fail() },
        validator = validator,
        readOnly = true,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    assertText("5")

    value = expected.first

    assertText(expected.second)
    assertEquals(expected.first, value, .0)
  }

  @Test
  fun `readonly when value is not finite`() {
    var value by mutableStateOf<Double?>(0.0)
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { value = it },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")

    value = Double.NaN

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }

    value = Double.POSITIVE_INFINITY

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }

    value = Double.NEGATIVE_INFINITY

    assertThrows(AssertionError::class.java) {
      rule.onNodeWithTag(TestTags.NODE.name).performTextInput(text = "5")
    }
  }

  @Test
  fun `change valid value`() {
    var value by mutableStateOf<Double?>(0.0)

    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { fail() },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    val inputOutputs = listOf(
      1.0 to "1",
      -1.0 to "-1",
      1.99999 to "1.99999",
      -1.99999 to "-1.99999",
      Double.MAX_VALUE to Double.MAX_VALUE.toBigDecimal().toPlainString(),
      -Double.MAX_VALUE to (-Double.MAX_VALUE).toBigDecimal().toPlainString(),
      Double.MIN_VALUE to Double.MIN_VALUE.toBigDecimal().toPlainString(),
      -Double.MIN_VALUE to (-Double.MIN_VALUE).toBigDecimal().toPlainString(),
      Double.Zero to "0",
      null to String.EMPTY
    )

    inputOutputs.forEach { (input, output) ->
      value = input
      assertText(output)
    }
  }

  @Test
  fun `change invalid value`() {
    var value by mutableStateOf(0.0)
    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { fail() },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    val inputOutputs = listOf(
      (Double.POSITIVE_INFINITY to Double.POSITIVE_INFINITY.toString()),
      (Double.NEGATIVE_INFINITY to Double.NEGATIVE_INFINITY.toString()),
      (Double.NaN to Double.NaN.toString()),
    )

    inputOutputs.forEach { (input, output) ->
      value = input
      assertText(output)
    }
  }
}