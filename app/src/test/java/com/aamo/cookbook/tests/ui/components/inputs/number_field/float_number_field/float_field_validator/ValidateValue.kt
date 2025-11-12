@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.number_field.float_number_field.float_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.FloatFieldValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ValidateValue {
  @Test
  fun `validate valid value`() {
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
      var text: String? = null
      assertTrue(FloatFieldValidator.onValid(value = input) { text = it })
      assertEquals(output, text)
    }
  }

  @Test
  fun `validate invalid value`() {
    val inputs = listOf(
      Float.POSITIVE_INFINITY,
      Float.NEGATIVE_INFINITY,
      Float.NaN,
    )

    inputs.forEach { input ->
      assertFalse(FloatFieldValidator.onValid(value = input) { fail() })
    }
  }
}