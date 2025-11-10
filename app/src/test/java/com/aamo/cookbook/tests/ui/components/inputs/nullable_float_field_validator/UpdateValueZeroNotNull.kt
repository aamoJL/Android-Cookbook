package com.aamo.cookbook.tests.ui.components.inputs.nullable_float_field_validator

import com.aamo.cookbook.ui.components.inputs.NullableFloatFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateValueZeroNotNull {
  val validator = NullableFloatFieldValidator(zeroEqualsNull = false, update = { t, v ->
    value = v
    text = t
  })
  var value: Float? = 0f
  var text: String = "0"

  @Test
  fun `update positive integer`() {
    assertTrue(validator.update(new = 1f, old = value))
    assertEquals(1f, value)
    assertEquals("1", text)
  }

  @Test
  fun `update negative integer`() {
    assertTrue(validator.update(new = -1f, old = value))
    assertEquals(-1f, value)
    assertEquals("-1", text)
  }

  @Test
  fun `update positive decimal`() {
    assertTrue(validator.update(new = 1.99999f, old = value))
    assertEquals(1.99999f, value)
    assertEquals("1.99999", text)
  }

  @Test
  fun `update negative decimal`() {
    assertTrue(validator.update(new = -1.99999f, old = value))
    assertEquals(-1.99999f, value)
    assertEquals("-1.99999", text)
  }

  @Test
  fun `update positive max float`() {
    assertTrue(validator.update(new = Float.MAX_VALUE, old = value))
    assertEquals(Float.MAX_VALUE, value)
    assertEquals("340282350000000000000000000000000000000", text)
  }

  @Test
  fun `update negative max float`() {
    assertTrue(validator.update(new = -Float.MAX_VALUE, old = value))
    assertEquals(-Float.MAX_VALUE, value)
    assertEquals("-340282350000000000000000000000000000000", text)
  }

  @Test
  fun `update positive min float`() {
    assertTrue(validator.update(new = Float.MIN_VALUE, old = value))
    assertEquals(Float.MIN_VALUE, value)
    assertEquals("0.0000000000000000000000000000000000000000000014", text)
  }

  @Test
  fun `update negative min float`() {
    assertTrue(validator.update(new = -Float.MIN_VALUE, old = value))
    assertEquals(-Float.MIN_VALUE, value)
    assertEquals("-0.0000000000000000000000000000000000000000000014", text)
  }

  @Test
  fun `update zero`() {
    validator.update(1f, value)
    assertEquals(1f, value)

    assertTrue(validator.update(new = 0f, old = value))
    assertEquals(0f, value)
    assertEquals("0", text)
  }

  @Test
  fun `update same`() {
    assertFalse(validator.update(new = value, old = value))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update positive infinity`() {
    assertTrue(validator.update(new = Float.POSITIVE_INFINITY, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update negative infinity`() {
    assertTrue(validator.update(new = Float.NEGATIVE_INFINITY, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update NaN`() {
    assertTrue(validator.update(new = Float.NaN, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update null`() {
    assertTrue(validator.update(new = null, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update positive out of range`() {
    assertTrue(validator.update(new = 340282350000000000000000000000000000000999f, old = value))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }
}