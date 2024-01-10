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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.utility.trimFirst
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFloatField(
  initialValue: Float?,
  onValueChange: (Float?) -> Unit,
  label: String,
  modifier: Modifier = Modifier
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = initialValue?.toStringWithoutZero() ?: "",
        selection = when (initialValue) {
          null -> TextRange.Zero
          else -> TextRange(
            initialValue.toStringWithoutZero().length,
            initialValue.toStringWithoutZero().length
          )
        }
      )
    )
  }

  var lastTextValue by remember { mutableStateOf(initialValue?.toStringWithoutZero() ?: "") }
  val textFieldValue = textFieldValueState

  TextField(
    value = textFieldValue,
    onValueChange = { newState ->
      val text = newState.text

      // Check if the text has no more than one non-digit chars
      if ((text.trimFirst('-').filterNot { it.isDigit() }.length <= 1) && (text.trimFirst('-')
          .trimFirst('.').isEmpty() || text.toFloatOrNull() != null)
      ) {
        textFieldValueState = newState

        val valueChanged = lastTextValue != text
        lastTextValue = text

        if (valueChanged) {
          onValueChange(text.toFloatOrNull())
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier.fillMaxWidth()
  )
}

@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedFormFloatField(
  initialValue: Float?,
  onValueChange: (Float?) -> Unit,
  label: String,
  modifier: Modifier = Modifier
) {
  var textFieldValueState by remember {
    mutableStateOf(
      TextFieldValue(
        text = initialValue?.toStringWithoutZero() ?: "",
        selection = when (initialValue) {
          null -> TextRange.Zero
          else -> TextRange(
            initialValue.toStringWithoutZero().length,
            initialValue.toStringWithoutZero().length
          )
        }
      )
    )
  }

  var lastTextValue by remember { mutableStateOf(initialValue?.toStringWithoutZero() ?: "") }
  val textFieldValue = textFieldValueState

  OutlinedTextField(
    value = textFieldValue,
    onValueChange = { newState ->
      val text = newState.text
      val decimalSeparator = DecimalFormat().decimalFormatSymbols.decimalSeparator

      // Check if the text has no more than one non-digit chars
      if ((text.trimFirst('-').filterNot { it.isDigit() }.length <= 1) && (text.trimFirst('-')
          .trimFirst(decimalSeparator).isEmpty() || text.toFloatOrNull() != null)
      ) {
        textFieldValueState = newState

        val valueChanged = lastTextValue != text
        lastTextValue = text

        if (valueChanged) {
          onValueChange(text.toFloatOrNull())
        }
      }
    },
    singleLine = true,
    label = { Text(text = label) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = modifier.fillMaxWidth()
  )
}