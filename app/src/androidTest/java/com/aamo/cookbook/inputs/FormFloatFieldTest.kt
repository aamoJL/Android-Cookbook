package com.aamo.cookbook.inputs

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.ui.components.form.FormFloatField
import com.aamo.cookbook.ui.theme.CookbookTheme
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FormFloatFieldTest {
  private var value: Float? = null

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setupNavHost() {
    rule.setContent {

      CookbookTheme {
        FormFloatField(initialValue = value, onValueChange = {
          value = it
        }, label = "", modifier = Modifier.testTag("tag"))
      }
    }
  }

  @Test
  fun input_int() {
    val expected = 123
    getInput().performTextInput(expected.toString())

    TestCase.assertEquals(expected.toFloat(), value)
  }

  @Test
  fun input_float() {
    val expected = 1.5f
    getInput().performTextInput(expected.toString())

    TestCase.assertEquals(expected, value)
  }

  @Test
  fun input_character() {
    getInput().performTextInput("abc")

    TestCase.assertNull(value)
  }

  private fun getInput() = rule.onNodeWithTag("tag")
}