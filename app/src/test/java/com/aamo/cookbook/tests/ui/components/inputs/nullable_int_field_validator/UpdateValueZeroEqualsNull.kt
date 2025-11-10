package com.aamo.cookbook.tests.ui.components.inputs.nullable_int_field_validator

import com.aamo.cookbook.ui.components.inputs.NullableIntFieldValidator
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

class UpdateValueZeroEqualsNull {
  val validator = NullableIntFieldValidator(zeroEqualsNull = true, update = { t, v ->
    value = v
    text = t
  })
  var value: Int? = null
  var text: String = String.EMPTY

  @Test
  fun `update positive integer`() {
    Assert.assertTrue(validator.update(new = 1, old = value))
    Assert.assertEquals(1, value)
    Assert.assertEquals("1", text)
  }

  @Test
  fun `update negative integer`() {
    Assert.assertTrue(validator.update(new = -1, old = value))
    Assert.assertEquals(-1, value)
    Assert.assertEquals("-1", text)
  }

  @Test
  fun `update max int`() {
    Assert.assertTrue(validator.update(new = Int.MAX_VALUE, old = value))
    Assert.assertEquals(Int.MAX_VALUE, value)
    Assert.assertEquals("2147483647", text)
  }

  @Test
  fun `update min int`() {
    Assert.assertTrue(validator.update(new = Int.MIN_VALUE, old = value))
    Assert.assertEquals(Int.MIN_VALUE, value)
    Assert.assertEquals("-2147483648", text)
  }

  @Test
  fun `update zero`() {
    validator.update(1, value)
    Assert.assertEquals(1, value)

    Assert.assertTrue(validator.update(new = 0, old = value))
    Assert.assertEquals(null, value)
    Assert.assertEquals(String.EMPTY, text)
  }

  @Test
  fun `update same`() {
    Assert.assertFalse(validator.update(new = value, old = value))
    Assert.assertEquals(value, value)
    Assert.assertEquals(text, text)
  }

  @Test
  fun `update null`() {
    validator.update(1, value)
    Assert.assertEquals(1, value)

    Assert.assertTrue(validator.update(new = null, old = value))
    Assert.assertEquals(null, value)
    Assert.assertEquals(String.EMPTY, text)
  }

  // TODO out of range
}