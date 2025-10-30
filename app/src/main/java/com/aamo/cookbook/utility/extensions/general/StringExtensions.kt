package com.aamo.cookbook.utility.extensions.general

import java.util.UUID

@Suppress("SameReturnValue") val String.Companion.EMPTY: String get() = ""

/**
 * Returns the string as a [UUID] if possible, otherwise returns null
 */
fun String.toUUIDorNull(): UUID? {
  return try {
    UUID.fromString(this)
  }
  catch (e: IllegalArgumentException) {
    null
  }
}

/**
 * Returns a string having leading character of the given chars removed.
 */
fun String.trimFirst(vararg chars: Char): String {
  return this.firstOrNull()?.let {
    if (chars.contains(it)) this.drop(1)
    else this
  } ?: this
}

/**
 * Returns the string formatted as a label for an optional input field
 */
fun String.asOptionalLabel(): String = "(${this})"