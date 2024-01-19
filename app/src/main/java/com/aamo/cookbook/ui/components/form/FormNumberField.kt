package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly

@Composable
fun FormNumberField(
  value: Int?,
  onValueChange: (Int?) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  imeAction: ImeAction = ImeAction.Next
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

  TextField(
    value = textFieldValue,
    onValueChange = { newState ->
      if (newState.text.isDigitsOnly() || newState.text.isEmpty()) {
        textFieldValueState = newState

        val valueChanged = lastTextValue != newState.text
        lastTextValue = newState.text

        if (valueChanged) {
          onValueChange(newState.text.toIntOrNull())
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
    modifier = modifier.fillMaxWidth()
  )
}

@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormOutlinedNumberField(
  value: Int?,
  onValueChange: (Int?) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  imeAction: ImeAction = ImeAction.Next
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
    onValueChange = { newState ->
      if (newState.text.isDigitsOnly() || newState.text.isEmpty()) {
        textFieldValueState = newState

        val valueChanged = lastTextValue != newState.text
        lastTextValue = newState.text

        if (valueChanged) {
          onValueChange(newState.text.toIntOrNull())
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
    modifier = modifier.fillMaxWidth()
  )
}