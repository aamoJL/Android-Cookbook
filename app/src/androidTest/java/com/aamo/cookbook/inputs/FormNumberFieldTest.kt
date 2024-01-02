package com.aamo.cookbook.inputs

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.theme.CookbookTheme
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FormNumberFieldTest {
  private var value: Int? = null

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {

      CookbookTheme {
        FormNumberField(value = value, onValueChange = {
          value = it
        }, label = "", modifier = Modifier.testTag("tag"))
      }
    }
  }

  @Test
  fun input_int() {
    val expected = 123
    getInput().performTextInput(expected.toString())

    assertEquals(expected, value)
  }

  @Test
  fun input_float() {
    getInput().performTextInput(1.5f.toString())

    assertEquals(null, value)
  }

  @Test
  fun input_character() {
    getInput().performTextInput("abc")

    assertNull(value)
  }

  private fun getInput() = rule.onNodeWithTag("tag")
}