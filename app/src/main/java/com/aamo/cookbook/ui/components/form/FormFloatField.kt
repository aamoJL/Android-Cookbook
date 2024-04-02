package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.utility.trimFirst

class FormFloatFieldDefaults {
  companion object {
    val keyboardOptions: KeyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Number,
      imeAction = ImeAction.Next
    )
  }
}

@Composable
fun FormFloatField(
  value: Float?,
  modifier: Modifier = Modifier,
  onValueChange: (Float?) -> Unit,
  label: String,
  keyboardOptions: KeyboardOptions = FormFloatFieldDefaults.keyboardOptions
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = value?.toStringWithoutZero() ?: "",
        selection = when (value) {
          null -> TextRange.Zero
          else -> TextRange(
            value.toStringWithoutZero().length,
            value.toStringWithoutZero().length
          )
        }
      )
    )
  }
  var lastTextValue by remember { mutableStateOf(value?.toStringWithoutZero() ?: "") }
  var lastFloatValue by remember { mutableStateOf(value) }

  LaunchedEffect(value){
    // This launched effect needs to be here, so the text value of the TextField will change
    // if the value changes from outside of this composable
    if(lastTextValue.toFloatOrNull() != value)
      textFieldValueState = textFieldValueState.copy(text = value?.toString() ?: "")
  }

  TextField(
    value = textFieldValueState,
    onValueChange = { newState ->
      val text = newState.text

      val regex = "-?[0-9]*(\\.[0-9]*)?".toRegex()
      if(text.matches(regex)){
        textFieldValueState = newState

        val newValue = text.toFloatOrNull()
        lastTextValue = text

        if (newValue != value) {
          lastFloatValue = newValue
          onValueChange(newValue)
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = keyboardOptions,
    modifier = modifier.fillMaxWidth()
  )
}

@Suppress("unused")
@Composable
fun OutlinedFormFloatField(
  value: Float?,
  onValueChange: (Float?) -> Unit,
  label: String,
  modifier: Modifier = Modifier
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = value?.toStringWithoutZero() ?: "",
        selection = when (value) {
          null -> TextRange.Zero
          else -> TextRange(
            value.toStringWithoutZero().length,
            value.toStringWithoutZero().length
          )
        }
      )
    )
  }
  var lastTextValue by remember { mutableStateOf(value?.toStringWithoutZero() ?: "") }
  var lastFloatValue by remember { mutableStateOf(value) }

  LaunchedEffect(value != lastFloatValue){
    // This launched effect needs to be here, so the text value of the TextField will change
    // if the value changes from outside of this composable
    if(lastTextValue.toFloatOrNull() != value)
      textFieldValueState = textFieldValueState.copy(text = value?.toStringWithoutZero() ?: "")
  }

  OutlinedTextField(
    value = textFieldValueState,
    onValueChange = { newState ->
      val text = newState.text

      // Check if the text has no more than one non-digit chars
      if ((text.trimFirst('-').filterNot { it.isDigit() }.length <= 1) && (text.trimFirst('-')
          .trimFirst('.').isEmpty() || text.toFloatOrNull() != null)
      ) {
        textFieldValueState = newState

        val newValue = text.toFloatOrNull()
        lastTextValue = text

        if (newValue != value) {
          lastFloatValue = newValue
          onValueChange(newValue)
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier.fillMaxWidth()
  )
}