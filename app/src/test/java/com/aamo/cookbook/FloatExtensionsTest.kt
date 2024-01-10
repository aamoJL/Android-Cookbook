package com.aamo.cookbook

import com.aamo.cookbook.utility.toFractionFormattedString
import com.aamo.cookbook.utility.toStringWithoutZero
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FloatExtensionsTest {

  @Test
  fun toStringWithoutZero() {
    val one = listOf(
      1f, 01f, 1.0f, 1.00f, 01.00f
    )
    val zero = listOf(
      0f, 00f, 0.0f, 0.00f, 00.00f
    )
    val ten = listOf(
      10f, 010f, 10.0f
    )

    one.forEach { assert(it.toStringWithoutZero() == "1") }
    zero.forEach { assert(it.toStringWithoutZero() == "0") }
    ten.forEach { assert(it.toStringWithoutZero() == "10") }
    assert(010.01f.toStringWithoutZero() == "10.01")
  }

  @Test
  fun toFractionFormattedString() {
    assertEquals("1½", 1.5f.toFractionFormattedString())
    assertEquals("3¼", 3.25f.toFractionFormattedString())
    assertEquals("¾", 0.75f.toFractionFormattedString())
    assertEquals("0.4", 0.4f.toFractionFormattedString())
    assertEquals("6", 6.0f.toFractionFormattedString())
  }
}