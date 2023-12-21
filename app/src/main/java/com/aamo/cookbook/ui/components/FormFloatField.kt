package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFloatField(
  value: Float?,
  onValueChange: (Float?) -> Unit,
  label: String,
  modifier: Modifier = Modifier
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = value?.toString() ?: "",
        selection = when (value) {
          null -> TextRange.Zero
          else -> TextRange(value.toString().length, value.toString().length)
        }
      )
    )
  }
  var lastTextValue by remember(value) { mutableStateOf(value?.toString() ?: "") }

  val textFieldValue = textFieldValueState.copy(text = value?.toString() ?: "")

  OutlinedTextField(
    value = textFieldValue,
    onValueChange = { newTextFieldValueState ->
      textFieldValueState = newTextFieldValueState

      val valueChanged = lastTextValue != newTextFieldValueState.text
      lastTextValue = newTextFieldValueState.text

      if (valueChanged) {
        onValueChange(newTextFieldValueState.text.toFloatOrNull())
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier.fillMaxWidth()
  )
}