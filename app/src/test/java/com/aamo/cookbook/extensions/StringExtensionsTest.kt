package com.aamo.cookbook.extensions

import com.aamo.cookbook.utility.extensions.general.toUUIDorNull
import com.aamo.cookbook.utility.extensions.general.trimFirst
import org.junit.Test
import java.util.UUID

class StringExtensionsTest {

  @Test
  fun toUUIDorNull() {
    val nullString = "asd"
    val uuid = UUID.randomUUID()
    val uuidString = uuid.toString()

    assert(nullString.toUUIDorNull() == null)
    assert(uuidString.toUUIDorNull() == uuid)
  }

  @Test
  fun trimFirst() {
    val string = "abc"

    assert(string.trimFirst('a') == "bc")
    assert(string.trimFirst('c') == "abc")
    assert(string.trimFirst('a', 'b') == "bc")
  }
}