package com.aamo.cookbook.tests.ui.components.inputs.int_number_field.int_field_validator

import com.aamo.cookbook.ui.components.inputs.IntFieldValidator
import org.junit.Assert
import org.junit.Test

class ValidateValue {
  val validator = IntFieldValidator()

  @Test
  fun `validate positive integer`() {
    val expected = 1 to "1"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }

  @Test
  fun `validate negative integer`() {
    val expected = -1 to "-1"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }

  @Test
  fun `validate positive max int`() {
    val expected = Int.MAX_VALUE to "2147483647"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }

  @Test
  fun `validate negative max int`() {
    val expected = -Int.MAX_VALUE to "-2147483647"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }

  @Test
  fun `validate positive min int`() {
    val expected = Int.MIN_VALUE to "-2147483648"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }

  @Test
  fun `validate zero`() {
    val expected = 0 to "0"
    var text: String? = null

    validator.onValid(value = expected.first) { text = it }
    Assert.assertEquals(expected.second, text)
  }
}