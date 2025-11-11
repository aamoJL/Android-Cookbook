@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.tests.ui.components.inputs.int_number_field.int_field_validator

import com.aamo.cookbook.ui.components.inputs.IntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class ValidateText {
  val validator = IntFieldValidator()

  @Test
  fun `validate positive integer`() {
    val expected = "100" to 100
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate negative integer`() {
    val expected = "-100" to -100
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate positive decimal`() {
    "1.99999".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate negative decimal`() {
    "-1.99999".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate positive max int`() {
    val expected = "2147483647" to Int.MAX_VALUE
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate negative max int`() {
    val expected = "-2147483647" to -Int.MAX_VALUE
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate min int`() {
    val expected = "-2147483648" to Int.MIN_VALUE
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate zero`() {
    val expected = "0" to 0
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.first, text)
    assertEquals(expected.second, value)
  }

  @Test
  fun `validate empty`() {
    val expected = String.EMPTY to ("0" to 0)
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.second.first, text)
    assertEquals(expected.second.second, value)
  }

  @Test
  fun `validate multiple zeros`() {
    val expected = "000" to ("0" to 0)
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.second.first, text)
    assertEquals(expected.second.second, value)
  }

  @Test
  fun `validate decimal of zero`() {
    "0.0".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate decimal of multiple zeros`() {
    "000.000".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate dot`() {
    ".".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate dot with zeroes`() {
    ".00".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate decimal with trailing zeroes`() {
    "0.100".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate integer with leading zeroes`() {
    val expected = "00100" to ("100" to 100)
    var value: Int? = null
    var text: String? = null

    validator.onValid(text = expected.first) { v, t -> value = v; text = t }
    assertEquals(expected.second.first, text)
    assertEquals(expected.second.second, value)
  }

  @Test
  fun `validate positive out of range`() {
    "9990282350000000000000000000000000000000".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate negative out of range`() {
    "-9990282350000000000000000000000000000000".also { validator.onValid(it) { _, _ -> fail() } }
  }

  @Test
  fun `validate invalid numeral`() {
    "..".also { validator.onValid(it) { _, _ -> fail() } }
    ".-1".also { validator.onValid(it) { _, _ -> fail() } }
    "1.0.0".also { validator.onValid(it) { _, _ -> fail() } }
    "1 2".also { validator.onValid(it) { _, _ -> fail() } }
    " ".also { validator.onValid(it) { _, _ -> fail() } }
    "test".also { validator.onValid(it) { _, _ -> fail() } }
    "12f".also { validator.onValid(it) { _, _ -> fail() } }
    "--12".also { validator.onValid(it) { _, _ -> fail() } }
    "-.-12".also { validator.onValid(it) { _, _ -> fail() } }
    "1-2".also { validator.onValid(it) { _, _ -> fail() } }
  }
}