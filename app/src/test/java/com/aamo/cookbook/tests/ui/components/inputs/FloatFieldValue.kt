@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs

import com.aamo.cookbook.ui.components.inputs.FloatFieldValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FloatFieldValueTests {
  @Test
  fun `sets correct value when updating value`() {
    FloatFieldValue().apply {
      assertEquals(0f, this.value)

      1f.also { value -> assertTrue(update(value).also { assertEquals(value, this.value) }) }
      (-1f).also { value -> assertTrue(update(value).also { assertEquals(value, this.value) }) }
      1.99999f.also { value -> assertTrue(update(value).also { assertEquals(value, this.value) }) }
      (-1.9999f).also { value ->
        assertTrue(update(value).also { assertEquals(value, this.value) })
      }
      Float.MAX_VALUE.also { value ->
        assertTrue(update(value).also { assertEquals(value, this.value) })
      }
      (Float.MAX_VALUE * -1).also { value ->
        assertTrue(update(value).also { assertEquals(value, this.value) })
      }
      0f.also { value -> assertTrue(update(value).also { assertEquals(value, this.value) }) }
    }
  }

  @Test
  fun `does not set value when updating invalid value`() {
    FloatFieldValue().apply {
      this.value.also { value ->
        assertFalse(update(value)).also { assertEquals(0f, this.value) }
      }
      Float.POSITIVE_INFINITY.also { value ->
        assertFalse(update(value)).also { assertEquals(0f, this.value) }
      }
      Float.NEGATIVE_INFINITY.also { value ->
        assertFalse(update(value)).also { assertEquals(0f, this.value) }
      }
      Float.NaN.also { value ->
        assertFalse(update(value)).also { assertEquals(0f, this.value) }
      }
    }
  }

  @Test
  fun `sets correct text when updating value`() {
    FloatFieldValue().apply {
      assertEquals("0", this.text)

      1f.also { value -> assertTrue(update(value).also { assertEquals("1", this.text) }) }
      (-1f).also { value -> assertTrue(update(value).also { assertEquals("-1", this.text) }) }
      1.99999f.also { value ->
        assertTrue(update(value).also { assertEquals("1.99999", this.text) })
      }
      (-1.9999f).also { value ->
        assertTrue(update(value).also { assertEquals("-1.9999", this.text) })
      }
      Float.MAX_VALUE.also { value ->
        assertTrue(update(value).also {
          assertEquals("340282350000000000000000000000000000000", this.text)
        })
      }
      (Float.MAX_VALUE * -1).also { value ->
        assertTrue(update(value).also {
          assertEquals("-340282350000000000000000000000000000000", this.text)
        })
      }
      0f.also { value -> assertTrue(update(value).also { assertEquals("0", this.text) }) }
    }
  }

  @Test
  fun `does not set text when updating invalid value`() {
    FloatFieldValue().apply {
      this.value.also { value ->
        assertFalse(update(value)).also { assertEquals("0", this.text) }
      }
      Float.POSITIVE_INFINITY.also { value ->
        assertFalse(update(value)).also { assertEquals("0", this.text) }
      }
      Float.NEGATIVE_INFINITY.also { value ->
        assertFalse(update(value)).also { assertEquals("0", this.text) }
      }
      Float.NaN.also { value ->
        assertFalse(update(value)).also { assertEquals("0", this.text) }
      }
    }
  }

  @Test
  fun `sets correct text when updating text`() {
    FloatFieldValue().apply {
      assertEquals("0", this.text)

      "1".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      "-1".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      "1.99999".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      "-1.9999".also { value ->
        assertTrue(update(value).also { assertEquals(value, this.text) })
      }
      "340282350000000000000000000000000000000".also { value ->
        assertTrue(update(value).also { assertEquals(value, this.text) })
      }
      "-340282350000000000000000000000000000000".also { value ->
        assertTrue(update(value).also { assertEquals(value, this.text) })
      }
      update(1f).also {
        "0".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      }
      update(1f).also {
        "000".also { value -> assertTrue(update(value).also { assertEquals("0", this.text) }) }
      }
      update(1f).also {
        ".00".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      }
      update(1f).also {
        "00.00".also { value -> assertTrue(update(value).also { assertEquals("0.00", this.text) }) }
      }
      "100".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      update(1f).also {
        "0.100".also { value -> assertTrue(update(value).also { assertEquals(value, this.text) }) }
      }
      update(1f).also {
        "0100".also { value -> assertTrue(update(value).also { assertEquals("100", this.text) }) }
      }
    }
  }

  @Test
  fun `does not set text when updating invalid text`() {
    FloatFieldValue().apply {
      this.text.also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals("0", this.text) })
      }
      "-9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals("0", this.text) })
      }
      "..".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      ".-1".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "1.0.0".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "1 2".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      " ".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "test".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "12f".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "--12".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "-.-12".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
      "1-2".also { value -> assertFalse(update(value).also { assertEquals("0", this.text) }) }
    }
  }

  @Test
  fun `does not set value when updating invalid text`() {
    FloatFieldValue().apply {
      this.text.also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      "9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(0f, this.value) })
      }
      "-9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(0f, this.value) })
      }
      "..".also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      ".-1".also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      "1.0.0".also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      "1 2".also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      " ".also { value -> assertFalse(update(value).also { assertEquals(0f, this.value) }) }
      String.EMPTY.also { value ->
        assertFalse(update(value).also { assertEquals(0f, this.value) })
      }
    }
  }
}