package com.aamo.cookbook.ui_tests.ui.components.inputs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.aamo.cookbook.ui.components.inputs.CountInput
import com.aamo.cookbook.ui.components.inputs.CountInputTags
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CountInput {
  @get:Rule val rule = createComposeRule()

  @Test
  fun `init value`() {
    val expected = 3 to "3"
    rule.setContent {
      CountInput(value = expected.first, onValueChange = {})
    }
    rule.onNodeWithTag(CountInputTags.VALUE.name).assert(hasText(expected.second))
  }

  @Test
  fun `onValueChange called`() {
    var called = false
    rule.setContent {
      CountInput(value = 0, onValueChange = { called = true })
    }
    rule.onNodeWithTag(CountInputTags.INCREASE.name).performClick()
    assert(called)
  }

  @Test
  fun increase() {
    val expected = 1 to "1"
    var value by mutableStateOf(expected.first - 1)
    rule.setContent {
      CountInput(value = value, onValueChange = { value = it })
    }
    rule.onNodeWithTag(CountInputTags.INCREASE.name).performClick()

    assertEquals(expected.first, value)
    rule.onNodeWithTag(CountInputTags.VALUE.name).assert(hasText(expected.second))
  }

  @Test
  fun decrease() {
    val expected = 1 to "1"
    var value by mutableStateOf(expected.first + 1)
    rule.setContent {
      CountInput(value = value, onValueChange = { value = it })
    }
    rule.onNodeWithTag(CountInputTags.DECREASE.name).performClick()

    assertEquals(expected.first, value)
    rule.onNodeWithTag(CountInputTags.VALUE.name).assert(hasText(expected.second))
  }

  @Suppress("HardCodedStringLiteral")
  @Test
  fun `label visibility`() {
    var expected by mutableStateOf("Title")
    rule.setContent {
      CountInput(value = 0, onValueChange = { }, label = expected)
    }

    rule.onNodeWithTag(CountInputTags.TITLE.name).assert(hasText(expected))

    expected = String.EMPTY

    rule.onNodeWithTag(CountInputTags.TITLE.name).assertDoesNotExist()
  }

  @Test
  fun `max value`() {
    val expected = 1 to "1"
    var value by mutableStateOf(expected.first)
    rule.setContent {
      CountInput(value = value, onValueChange = { value = it }, maxValue = 1)
    }
    rule.onNodeWithTag(CountInputTags.INCREASE.name).performClick()

    assertEquals(expected.first, value)
    rule.onNodeWithTag(CountInputTags.VALUE.name).assert(hasText(expected.second))
  }

  @Test
  fun `min value`() {
    val expected = 1 to "1"
    var value by mutableStateOf(expected.first)
    rule.setContent {
      CountInput(value = value, onValueChange = { value = it }, minValue = 1)
    }
    rule.onNodeWithTag(CountInputTags.DECREASE.name).performClick()

    assertEquals(expected.first, value)
    rule.onNodeWithTag(CountInputTags.VALUE.name).assert(hasText(expected.second))
  }
}