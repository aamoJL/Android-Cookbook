package com.aamo.cookbook.tests.utility.extensions.general.string_extensions

import com.aamo.cookbook.utility.extensions.general.toUUIDorNull
import org.junit.Test
import java.util.UUID

class ToUUUIDorNull {
  @Test
  fun toUUIDorNull() {
    val nullString = "asd"
    val uuid = UUID.randomUUID()
    val uuidString = uuid.toString()

    assert(nullString.toUUIDorNull() == null)
    assert(uuidString.toUUIDorNull() == uuid)
  }
}