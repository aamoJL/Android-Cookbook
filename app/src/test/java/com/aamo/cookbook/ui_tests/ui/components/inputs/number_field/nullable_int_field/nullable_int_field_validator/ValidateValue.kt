package com.aamo.cookbook.ui_tests.ui.components.inputs.number_field.nullable_int_field.nullable_int_field_validator

import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidateValue {
  @Test
  fun `validate valid value`() {
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
      var text: String? = null
      assertTrue(NullableIntFieldValidator.onValid(value = input) { text = it })
      Assert.assertEquals(output, text)
    }
  }
}