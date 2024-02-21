package com.aamo.cookbook.extensions

import com.aamo.cookbook.utility.swap
import org.junit.Assert.assertEquals
import org.junit.Test

class MutableListExtensionsTest {
  @Test
  fun swap_adjacent() {
    val actual = mutableListOf(
      0, 1, 2, 3
    ).apply { swap(1, 2) }

    val expected = listOf(
      0, 2, 1, 3
    )

    assertEquals(expected, actual)
  }

  @Test
  fun swap_apart() {
    val actual = mutableListOf(
      0, 1, 2, 3
    ).apply { swap(0, 3) }

    val expected = listOf(
      3, 1, 2, 0
    )

    assertEquals(expected, actual)
  }
}