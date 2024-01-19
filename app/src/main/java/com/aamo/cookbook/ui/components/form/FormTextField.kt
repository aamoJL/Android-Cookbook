package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

class FormTextFieldDefaults {
  companion object {
    val keyboardOptions: KeyboardOptions = KeyboardOptions(
      capitalization = KeyboardCapitalization.Sentences,
      autoCorrect = true,
      keyboardType = KeyboardType.Text,
      imeAction = ImeAction.Next
    )
  }
}

@Composable
fun FormTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = FormTextFieldDefaults.keyboardOptions,
  trailingIcon: @Composable (() -> Unit)? = null
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = value, selection = when {
          value.isEmpty() -> TextRange.Zero
          else -> TextRange(value.length, value.length)
        }
      )
    )
  }
  var lastTextValue by remember(value) { mutableStateOf(value) }

  val textFieldValue = textFieldValueState.copy(text = value)

  TextField(
    value = textFieldValue,
    onValueChange = { newTextFieldValueState ->
      textFieldValueState = newTextFieldValueState

      val valueChanged = lastTextValue != newTextFieldValueState.text
      lastTextValue = newTextFieldValueState.text

      if (valueChanged) {
        onValueChange(newTextFieldValueState.text)
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = keyboardOptions,
    trailingIcon = trailingIcon,
    modifier = modifier.fillMaxWidth()
  )
}

@Suppress("unused")
@Composable
fun FormOutlinedTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = FormTextFieldDefaults.keyboardOptions,
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = value, selection = when {
          value.isEmpty() -> TextRange.Zero
          else -> TextRange(value.length, value.length)
        }
      )
    )
  }
  var lastTextValue by remember(value) { mutableStateOf(value) }

  val textFieldValue = textFieldValueState.copy(text = value)

  OutlinedTextField(
    value = textFieldValue,
    onValueChange = { newTextFieldValueState ->
      textFieldValueState = newTextFieldValueState

      val valueChanged = lastTextValue != newTextFieldValueState.text
      lastTextValue = newTextFieldValueState.text

      if (valueChanged) {
        onValueChange(newTextFieldValueState.text)
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = keyboardOptions,
    modifier = modifier.fillMaxWidth()
  )
}