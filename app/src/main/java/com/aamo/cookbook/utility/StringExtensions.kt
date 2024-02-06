package com.aamo.cookbook.utility

import java.util.UUID

/**
 * Returns the string as a [UUID] if possible, otherwise returns null
 */
fun String.toUUIDorNull(): UUID? {
  return try {
    UUID.fromString(this)
  } catch (e: IllegalArgumentException) {
    null
  }
}

/**
 * Returns a string having leading character of the given chars removed.
 */
fun String.trimFirst(vararg chars: Char, ignoreCase: Boolean = true): String {
  chars.forEach {
    if (this.startsWith(it, ignoreCase)) {
      return this.drop(1)
    }
  }
  return this
}

/**
 * Returns the string formatted as a label for an optional input field
 */
fun String.asOptionalLabel(): String = "(${this})"