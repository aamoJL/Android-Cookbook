package com.aamo.cookbook.utility

fun Float.toStringWithoutZero(): String {
  return this.toString().trimEnd { it == '0' }.trimEnd { it == '.' }.trimEnd { it == ',' }
}