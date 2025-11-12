package com.aamo.cookbook.tests.ui.components.inputs.number_field.nullable_int_field.nullable_int_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

class ValidateText {
  val validator = NullableIntFieldValidator

  @Test
  fun `validate valid text`() {
    val inputOutputs = listOf(
      "100" to ("100" to 100),
      "-100" to ("-100" to -100),
      "2147483647" to ("2147483647" to Int.MAX_VALUE),
      "-2147483647" to ("-2147483647" to -Int.MAX_VALUE),
      "-2147483648" to ("-2147483648" to Int.MIN_VALUE),
      String.EMPTY to (String.EMPTY to null),
      "0" to ("0" to 0),
      "000" to ("0" to 0),
      "00100" to ("100" to 100),
      "0-" to ("-" to null),
      "00-" to ("-" to null),
      "-" to ("-" to null),
    )

    inputOutputs.forEach { (input, output) ->
      var value: Int? = null
      var text: String? = null

      validator.onValid(text = input) { v, t -> value = v; text = t }
      Assert.assertEquals(output.first, text)
      Assert.assertEquals(output.second, value)
    }
  }

  @Suppress("HardCodedStringLiteral")
  @Test
  fun `validate invalid text`() {
    val inputs = listOf(
      "1.99999",
      "-1.99999",
      "0.0",
      "000.000",
      "0.100",
      "9990282350000000000000000000000000000000",
      "-9990282350000000000000000000000000000000",
      ".",
      ".00",
      "..",
      ".-1",
      "1.0.0",
      "1 2",
      " ",
      "test",
      "12f",
      "--12",
      "-.-12",
      "1-2",
    )

    inputs.forEach { input ->
      validator.onValid(input) { _, _ -> Assert.fail() }
    }
  }
}