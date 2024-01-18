package com.aamo.cookbook.utility

import java.math.BigDecimal
import java.math.RoundingMode

fun Float.toStringWithoutZero(
  decimalCount: Int = 2,
  roundingMode: RoundingMode = RoundingMode.HALF_UP
): String {
  // Without converting to big decimal the string could be converted
  // to scientific notation (e.g. "1.2E8")
  return this.toBigDecimal().setScale(decimalCount, roundingMode).toPlainString().trimEnd { it == '0' }.trimEnd { it == '.' }
    .trimEnd { it == ',' }
}

/**
 * Returns float value as a string where the fractions has been changed to fraction character
 * if the values fractions match any of the common fraction values.
 * Otherwise the value will be formatted without fraction zeroes
 *
 * @param roundToNearestFraction if true, the value will be rounded to the smaller fraction. Zero will be rounded to the first fraction
 */
fun Float.toFractionFormattedString(roundToNearestFraction: Boolean = true) : String {
  // Pair of decimal values and fraction chars
  val fractions = setOf(
    Pair(.25, '¼'),
    Pair(.33, '⅓'),
    Pair(.50, '½'),
    Pair(.66, '⅔'),
    Pair(.67, '⅔'),
    Pair(.75, '¾'),
    Pair(.76, '¾'),
  )

  val bigDecimal = this.toBigDecimal()
  val integers = bigDecimal.toBigInteger().toInt()
  val decimals = bigDecimal.subtract(integers.toBigDecimal())

  if(decimals.compareTo(BigDecimal.ZERO) == 0) return integers.toString()

  val char: Char? = roundToNearestFraction.let { round ->
    if (round) {
      if (decimals > fractions.last().first.toBigDecimal()) { return@let null }
      fractions.firstOrNull { pair -> pair.first.toBigDecimal() >= decimals }?.second
    } else {
      fractions.firstOrNull { pair -> pair.first.toBigDecimal() == decimals }?.second
    }
  }

  return if(roundToNearestFraction) {
    if(char != null) {
      // Fraction char was found from the list
      if(integers == 0) char.toString()
      else integers.toString().plus(' ').plus(char)
    }
    else {
      // Fraction char was not found from the list
      // value will be rounded up, if the value is over the fraction list values
      if (decimals > fractions.last().first.toBigDecimal()) { (integers + 1).toString() }
      else { integers.toString() }
    }
  }
  else {
    if(char != null) {
      if(integers == 0) char.toString()
      else integers.toString().plus(' ').plus(char)
    }
    else this.toStringWithoutZero()
  }
}