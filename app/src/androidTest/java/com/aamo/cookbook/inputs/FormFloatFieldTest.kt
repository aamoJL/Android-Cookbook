package com.aamo.cookbook.inputs

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.ui.components.form.FormFloatField
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.toStringWithoutZero
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FormFloatFieldTest {
  private var value: Float? by mutableStateOf(null)

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        FormFloatField(
          value = value,
          onValueChange = { value = it },
          label = "",
          modifier = Modifier.testTag("tag"))
      }
    }
  }

  @Test
  fun input_int() {
    val expected = 123
    getInput().performTextInput(expected.toString())

    assertEquals(expected.toFloat(), value)
  }

  @Test
  fun input_float() {
    val expected = 1.5f
    getInput().performTextInput(expected.toString())

    assertEquals(expected, value)
  }

  @Test
  fun input_character() {
    getInput().performTextInput("abc")

    assertNull(value)
  }

  @Test
  fun changeValue() {
    val newValue = 20f
    value = newValue

    getInput().assertTextContains(newValue.toStringWithoutZero())
  }

  private fun getInput() = rule.onNodeWithTag("tag")
}