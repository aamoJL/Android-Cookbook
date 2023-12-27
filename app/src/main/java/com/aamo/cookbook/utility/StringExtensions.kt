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