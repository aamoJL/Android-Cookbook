@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.number_field.nullable_float_field.nullable_float_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.NullableFloatFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidateText {
  @Test
  fun `validate valid text`() {
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
      String.EMPTY to (String.EMPTY to null),
      "000" to ("0" to 0f),
      "0.0" to ("0.0" to 0f),
      "000.000" to ("0.000" to 0f),
      "." to ("." to null),
      "-." to ("-." to null),
      ".00" to (".00" to 0f),
      "0.100" to ("0.100" to 0.1f),
      "00100" to ("100" to 100f),
      "0-" to ("-" to null),
    )

    inputOutputs.forEach { (input, outputs) ->
      var text: String? = null
      var value: Float? = null

      NullableFloatFieldValidator.onValid(text = input) { v, t -> value = v; text = t }
        .also { assertEquals(outputs.first, text); assertEquals(outputs.second, value) }
    }
  }

  @Test
  fun `validate invalid text`() {
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

    inputs.forEach { input ->
      NullableFloatFieldValidator.onValid(text = input) { _, _ -> fail() }
    }
  }
}