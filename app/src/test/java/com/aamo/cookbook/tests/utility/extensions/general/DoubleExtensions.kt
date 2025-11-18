package com.aamo.cookbook.tests.utility.extensions.general

import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.extensions.general.toStringWithoutZero
import junit.framework.TestCase.assertEquals
import org.junit.Test

class DoubleExtensions {
  @Test
  fun toStringWithoutZero() {
    val one = listOf(
      1.0, 01.0, 1.0, 1.00, 01.00
    )
    val zero = listOf(
      0.0, 00.0, 0.0, 0.00, 00.00
    )
    val ten = listOf(
      10.0, 010.0, 10.0
    )

    one.forEach { assert(it.toStringWithoutZero() == "1") }
    zero.forEach { assert(it.toStringWithoutZero() == "0") }
    ten.forEach { assert(it.toStringWithoutZero() == "10") }
    assert(010.01.toStringWithoutZero() == "10.01")
  }

  @Test
  fun toFractionFormattedString_RoundToNearestFraction() {
    assertEquals("0", 0.0.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("¼", 0.1.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("1 ½", 1.5.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("3 ¼", 3.25.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("¾", 0.75.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("½", 0.4.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("6", 6.0.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("1", .99.toFractionFormattedString(roundToNearestFraction = true))
    assertEquals("1", 1.0.toFractionFormattedString(roundToNearestFraction = true))
  }

  @Test
  fun toFractionFormattedString() {
    assertEquals("1 ½", 1.5.toFractionFormattedString(roundToNearestFraction = false))
    assertEquals("3 ¼", 3.25.toFractionFormattedString(roundToNearestFraction = false))
    assertEquals("¾", 0.75.toFractionFormattedString(roundToNearestFraction = false))
    assertEquals("0.4", 0.4.toFractionFormattedString(roundToNearestFraction = false))
    assertEquals("6", 6.0.toFractionFormattedString(roundToNearestFraction = false))
    assertEquals("0.99", .99.toFractionFormattedString(roundToNearestFraction = false))
  }
}