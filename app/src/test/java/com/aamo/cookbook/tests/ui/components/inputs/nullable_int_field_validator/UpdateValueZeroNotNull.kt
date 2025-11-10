package com.aamo.cookbook.tests.ui.components.inputs.nullable_int_field_validator

import com.aamo.cookbook.ui.components.inputs.NullableIntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateValueZeroNotNull {
  val validator = NullableIntFieldValidator(zeroEqualsNull = false, update = { t, v ->
    value = v
    text = t
  })
  var value: Int? = 0
  var text: String = "0"

  @Test
  fun `update positive integer`() {
    assertTrue(validator.update(new = 1, old = value))
    assertEquals(1, value)
    assertEquals("1", text)
  }

  @Test
  fun `update negative integer`() {
    assertTrue(validator.update(new = -1, old = value))
    assertEquals(-1, value)
    assertEquals("-1", text)
  }

  @Test
  fun `update positive max int`() {
    assertTrue(validator.update(new = Int.MAX_VALUE, old = value))
    assertEquals(Int.MAX_VALUE, value)
    assertEquals("2147483647", text)
  }

  @Test
  fun `update negative max int`() {
    assertTrue(validator.update(new = -Int.MAX_VALUE, old = value))
    assertEquals(-Int.MAX_VALUE, value)
    assertEquals("-2147483647", text)
  }

  @Test
  fun `update positive min int`() {
    assertTrue(validator.update(new = Int.MIN_VALUE, old = value))
    assertEquals(Int.MIN_VALUE, value)
    assertEquals("-2147483648", text)
  }

  @Test
  fun `update zero`() {
    validator.update(1, value)
    assertEquals(1, value)

    assertTrue(validator.update(new = 0, old = value))
    assertEquals(0, value)
    assertEquals("0", text)
  }

  @Test
  fun `update same`() {
    assertFalse(validator.update(new = value, old = value))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update null`() {
    assertTrue(validator.update(new = null, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update positive out of range`() {
    assertFalse(validator.update(new = "9990282350000000000000000000000000000000", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update negative out of range`() {
    assertFalse(validator.update(new = "-9990282350000000000000000000000000000000", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }
}