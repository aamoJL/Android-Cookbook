package com.aamo.cookbook.ui_tests.ui.components.inputs.number_field.nullable_int_field

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.aamo.cookbook.test_utility.ui.TestTags
import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SetValue {
  @get:Rule val rule = createComposeRule()

  val validator = NullableIntFieldValidator

  private fun assertText(text: String) {
    rule.onNodeWithTag(TestTags.NODE.name).assert(hasText(text))
  }

  @Test
  fun `initial render with value`() {
    val expected = 0 to "0"
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
    val expected = 10 to "10"
    var value by mutableStateOf(5)
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
    val expected = 0 to "0"
    var value by mutableStateOf(5)
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
    assertEquals(expected.first, value)
  }

  @Test
  fun `change valid value`() {
    var value by mutableStateOf<Int?>(0)

    rule.setContent {
      NumberField(
        value = value,
        onValueChange = { fail() },
        validator = validator,
        modifier = Modifier.testTag(TestTags.NODE.name)
      )
    }

    val inputOutputs = listOf(
      0 to "0",
      1 to "1",
      -1 to "-1",
      Int.MAX_VALUE to "2147483647",
      -Int.MAX_VALUE to "-2147483647",
      Int.MIN_VALUE to "-2147483648",
      null to String.EMPTY
    )

    inputOutputs.forEach { (input, output) ->
      value = input
      assertText(output)
    }
  }
}