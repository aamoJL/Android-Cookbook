package com.aamo.cookbook.utility

fun Float.toStringWithoutZero(): String {
  // Without converting to big decimal the string could be converted
  // to scientific notation (e.g. "1.2E8")
  return this.toBigDecimal().toPlainString().trimEnd { it == '0' }.trimEnd { it == '.' }
    .trimEnd { it == ',' }
}

/**
 * Returns float value as a string where the fractions has been changed to fraction character
 * if the values fractions match any of the common fraction values.
 * Otherwise the value will be formatted without fraction zeroes
 */
fun Float.toFractionFormattedString() : String {
  val fractions = setOf(
    Pair(".5", '½'),
    Pair(".25", '¼'),
    Pair(".75",'¾')
  )
  val text = this.toStringWithoutZero()

  for (fraction in fractions){
    if(text.endsWith(fraction.first)){
      return text.dropLast(fraction.first.length).plus(fraction.second).trimStart('0')
    }
  }

  return text
}