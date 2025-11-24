@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.ui_tests.ui.components.inputs.number_field.nullable_double_field.nullable_double_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.NullableDoubleFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidateText {
  @Test
  fun `validate valid text`() {
    val inputOutputs = listOf(
      "100" to ("100" to 100.0),
      "-100" to ("-100" to -100.0),
      "1.999" to ("1.999" to 1.999),
      "-1.999" to ("-1.999" to -1.999),
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
      "0.100" to ("0.100" to 0.1),
      "00100" to ("100" to 100.0),
      "0-" to ("-" to null),
    )

    inputOutputs.forEach { (input, outputs) ->
      var text: String? = null
      var value: Double? = null

      NullableDoubleFieldValidator.onValid(text = input) { v, t -> value = v; text = t }
        .also { assertEquals(outputs.first, text); assertEquals(outputs.second, value) }
    }
  }

  @Test
  fun `validate invalid text`() {
    val inputs = listOf(
      Double.MAX_VALUE.toBigDecimal().toPlainString() + "000",
      (-Double.MAX_VALUE).toBigDecimal().toPlainString() + "000",
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

    inputs.forEach { input ->
      NullableDoubleFieldValidator.onValid(text = input) { _, s -> fail(s) }
    }
  }
}