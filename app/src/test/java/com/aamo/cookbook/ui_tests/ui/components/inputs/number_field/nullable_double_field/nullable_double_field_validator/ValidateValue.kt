@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.ui_tests.ui.components.inputs.number_field.nullable_double_field.nullable_double_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.NullableDoubleFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.Zero
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ValidateValue {
  @Test
  fun `validate valid value`() {
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
      var text: String? = null
      assertTrue(NullableDoubleFieldValidator.onValid(value = input) { text = it })
      assertEquals(output, text)
    }
  }

  @Test
  fun `validate invalid value`() {
    val inputs = listOf(
      Double.POSITIVE_INFINITY,
      Double.NEGATIVE_INFINITY,
      Double.NaN,
    )

    inputs.forEach { input ->
      assertFalse(NullableDoubleFieldValidator.onValid(value = input) { fail() })
    }
  }
}