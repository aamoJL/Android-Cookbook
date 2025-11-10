@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.nullable_float_field_validator

import com.aamo.cookbook.ui.components.inputs.NullableFloatFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateTextZeroEqualsNull {
  val validator = NullableFloatFieldValidator(zeroEqualsNull = true, update = { t, v ->
    value = v
    text = t
  })
  var value: Float? = null
  var text: String = String.EMPTY

  @Test
  fun `update positive integer`() {
    assertTrue(validator.update(new = "100", old = text))
    assertEquals(100f, value)
    assertEquals("100", text)
  }

  @Test
  fun `update negative integer`() {
    assertTrue(validator.update(new = "-100", old = text))
    assertEquals(-100f, value)
    assertEquals("-100", text)
  }

  @Test
  fun `update positive decimal`() {
    assertTrue(validator.update(new = "1.99999", old = text))
    assertEquals(1.99999f, value)
    assertEquals("1.99999", text)
  }

  @Test
  fun `update negative decimal`() {
    assertTrue(validator.update(new = "-1.99999", old = text))
    assertEquals(-1.99999f, value)
    assertEquals("-1.99999", text)
  }

  @Test
  fun `update positive max float`() {
    assertTrue(validator.update(new = "340282350000000000000000000000000000000", old = text))
    assertEquals(Float.MAX_VALUE, value)
    assertEquals("340282350000000000000000000000000000000", text)
  }

  @Test
  fun `update negative max float`() {
    assertTrue(validator.update(new = "-340282350000000000000000000000000000000", old = text))
    assertEquals(-Float.MAX_VALUE, value)
    assertEquals("-340282350000000000000000000000000000000", text)
  }

  @Test
  fun `update positive min float`() {
    assertTrue(
      validator.update(new = "0.0000000000000000000000000000000000000000000014", old = text)
    )
    assertEquals(Float.MIN_VALUE, value)
    assertEquals("0.0000000000000000000000000000000000000000000014", text)
  }

  @Test
  fun `update negative min float`() {
    assertTrue(
      validator.update(new = "-0.0000000000000000000000000000000000000000000014", old = text)
    )
    assertEquals(-Float.MIN_VALUE, value)
    assertEquals("-0.0000000000000000000000000000000000000000000014", text)
  }

  @Test
  fun `update zero`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = "0", old = text))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update empty`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = String.EMPTY, old = text))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update same`() {
    assertFalse(validator.update(new = text, old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update multiple zeros`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = "000", old = text))
    assertEquals(null, value)
    assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update decimal of zero`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = "0.0", old = text))
    assertEquals(null, value)
    assertEquals("0.0", text)
  }

  @Test
  fun `update decimal of multiple zeros`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = "000.000", old = text))
    assertEquals(null, value)
    assertEquals("0.000", text)
  }

  @Test
  fun `update dot`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = ".", old = text))
    assertEquals(null, value)
    assertEquals(".", text)
  }

  @Test
  fun `update dot with zeroes`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = ".00", old = text))
    assertEquals(null, value)
    assertEquals(".00", text)
  }

  @Test
  fun `update decimal with trailing zeroes`() {
    assertTrue(validator.update(new = "0.100", old = text))
    assertEquals(0.1f, value)
    assertEquals("0.100", text)
  }

  @Test
  fun `update integer with leading zeroes`() {
    assertTrue(validator.update(new = "00100", old = text))
    assertEquals(100f, value)
    assertEquals("100", text)
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

  @Test
  fun `update invalid numeral`() {
    validator.update(1f, value)

    assertFalse(validator.update(new = "..", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = ".-1", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "1.0.0", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "1 2", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = " ", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "..", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "null", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "12f", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "--12", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "-.-12", old = text)).also { assertEquals("1", text) }
    assertFalse(validator.update(new = "1-2", old = text)).also { assertEquals("1", text) }
  }
}