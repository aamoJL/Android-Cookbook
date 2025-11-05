@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs

import com.aamo.cookbook.ui.components.inputs.NullableFloatFieldValue
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class NullableFloatFieldValueTests {
  @Test
  fun `sets correct value when updating value`() {
    NullableFloatFieldValue().apply {
      assertEquals(null, this.value)

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
      update(1f).also {
        Float.POSITIVE_INFINITY.also { value ->
          assertTrue(update(value)).also { assertEquals(null, this.value) }
        }
      }
      update(1f).also {
        Float.NEGATIVE_INFINITY.also { value ->
          assertTrue(update(value)).also { assertEquals(null, this.value) }
        }
      }
      update(1f).also {
        Float.NaN.also { value ->
          assertTrue(update(value)).also { assertEquals(null, this.value) }
        }
      }
      update(1f).also {
        null.also { value -> assertTrue(update(value).also { assertEquals(value, this.value) }) }
      }
    }
  }

  @Test
  fun `does not set value when updating invalid value`() {
    NullableFloatFieldValue().apply {
      val default = 1f
      update(default)

      default.also { value ->
        assertFalse(update(value)).also { assertEquals(default, this.value) }
      }
    }
  }

  @Test
  fun `sets correct text when updating value`() {
    NullableFloatFieldValue().apply {
      assertEquals(String.EMPTY, this.text)

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
      update(1f).also {
        Float.POSITIVE_INFINITY.also { value ->
          assertTrue(update(value)).also { assertEquals(String.EMPTY, this.text) }
        }
      }
      update(1f).also {
        Float.NEGATIVE_INFINITY.also { value ->
          assertTrue(update(value)).also { assertEquals(String.EMPTY, this.text) }
        }
      }
      update(1f).also {
        Float.NaN.also { value ->
          assertTrue(update(value)).also { assertEquals(String.EMPTY, this.text) }
        }
      }
      update(1f).also {
        null.also { value ->
          assertTrue(update(value).also { assertEquals(String.EMPTY, this.text) })
        }
      }
    }
  }

  @Test
  fun `does not set text when updating invalid value`() {
    NullableFloatFieldValue().apply {
      val default = 1f
      update(default)

      default.also { value -> assertFalse(update(value)).also { assertEquals("1", this.text) } }
    }
  }

  @Test
  fun `sets correct text when updating text`() {
    NullableFloatFieldValue().apply {
      assertEquals(String.EMPTY, this.text)

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
      String.EMPTY.also { value ->
        assertTrue(update(value).also { assertEquals(value, this.text) })
      }
    }
  }

  @Test
  fun `does not set text when updating invalid text`() {
    NullableFloatFieldValue().apply {
      val default = "1"
      update(default)

      default.also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(default, this.text) })
      }
      "-9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(default, this.text) })
      }
      "..".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      ".-1".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "1.0.0".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "1 2".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      " ".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "test".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "12f".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "--12".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "-.-12".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      "1-2".also { value -> assertFalse(update(value).also { assertEquals(default, this.text) }) }
      update(null).also {
        assertFalse(update("asd").also { assertEquals(String.EMPTY, this.text) })
      }
      update(null).also {
        assertFalse(update("9990282350000000000000000000000000000000").also {
          assertEquals(String.EMPTY, this.text)
        })
      }
    }
  }

  @Test
  fun `does not set value when updating invalid text`() {
    NullableFloatFieldValue().apply {
      val default = "1"
      update(default)

      default.also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
      "9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(1f, this.value) })
      }
      "-9990282350000000000000000000000000000000".also { value ->
        assertFalse(update(value).also { assertEquals(1f, this.value) })
      }
      "..".also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
      ".-1".also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
      "1.0.0".also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
      "1 2".also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
      " ".also { value -> assertFalse(update(value).also { assertEquals(1f, this.value) }) }
    }
  }

  @Test
  fun `zero equals null`() {
    NullableFloatFieldValue(zeroEqualsNull = true).apply {
      assertEquals(null, this.value)
      assertEquals(String.EMPTY, this.text)

      update(1f).also {
        assertTrue(
          update(0f).also {
            assertEquals(null, this.value)
            assertEquals(String.EMPTY, this.text)
          })
      }

      update(1f).also {
        assertTrue(
          update("0").also {
            assertEquals(null, this.value)
            assertEquals(String.EMPTY, this.text)
          })
      }

      update(1f).also {
        assertTrue(
          update(".").also {
            assertEquals(null, this.value)
            assertEquals(".", this.text)
          })
      }

      update(1f).also {
        assertTrue(
          update(".0").also {
            assertEquals(null, this.value)
            assertEquals(".0", this.text)
          })
      }
    }
  }
}