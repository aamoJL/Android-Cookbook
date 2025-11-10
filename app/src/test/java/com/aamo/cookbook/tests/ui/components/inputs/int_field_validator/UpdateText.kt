@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.int_field_validator

import com.aamo.cookbook.ui.components.inputs.IntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateText {
  val validator = IntFieldValidator(update = { t, v ->
    value = v
    text = t
  })
  var value: Int = 0
  var text: String = "0"

  @Test
  fun `update positive integer`() {
    assertTrue(validator.update(new = "100", old = text))
    assertEquals(100, value)
    assertEquals("100", text)
  }

  @Test
  fun `update negative integer`() {
    assertTrue(validator.update(new = "-100", old = text))
    assertEquals(-100, value)
    assertEquals("-100", text)
  }

  @Test
  fun `update positive decimal`() {
    assertFalse(validator.update(new = "1.99999", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update negative decimal`() {
    assertFalse(validator.update(new = "-1.99999", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update positive max int`() {
    assertTrue(validator.update(new = "2147483647", old = text))
    assertEquals(Int.MAX_VALUE, value)
    assertEquals("2147483647", text)
  }

  @Test
  fun `update negative max int`() {
    assertTrue(validator.update(new = "-2147483647", old = text))
    assertEquals(-Int.MAX_VALUE, value)
    assertEquals("-2147483647", text)
  }

  @Test
  fun `update min int`() {
    assertTrue(
      validator.update(new = "-2147483648", old = text)
    )
    assertEquals(-Int.MIN_VALUE, value)
    assertEquals("-2147483648", text)
  }

  @Test
  fun `update zero`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = "0", old = text))
    assertEquals(0, value)
    assertEquals("0", text)
  }

  @Test
  fun `update empty`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertTrue(validator.update(new = String.EMPTY, old = text))
    assertEquals(0, value)
    assertEquals("0", text)
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
    assertEquals(0, value)
    assertEquals("0", text)
  }

  @Test
  fun `update decimal of zero`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertFalse(validator.update(new = "0.0", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update decimal of multiple zeros`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertFalse(validator.update(new = "000.000", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update dot`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertFalse(validator.update(new = ".", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update dot with zeroes`() {
    validator.update("1", text)
    assertEquals("1", text)

    assertFalse(validator.update(new = ".00", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update decimal with trailing zeroes`() {
    assertFalse(validator.update(new = "0.100", old = text))
    assertEquals(value, value)
    assertEquals(text, text)
  }

  @Test
  fun `update integer with leading zeroes`() {
    assertTrue(validator.update(new = "00100", old = text))
    assertEquals(100, value)
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
    assertFalse(validator.update(new = "..", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = ".-1", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "1.0.0", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "1 2", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = " ", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "..", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "test", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "12f", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "--12", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "-.-12", old = text)).also { assertEquals("0", text) }
    assertFalse(validator.update(new = "1-2", old = text)).also { assertEquals("0", text) }
  }
}