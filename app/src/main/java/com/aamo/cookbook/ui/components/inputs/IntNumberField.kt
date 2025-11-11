package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.aamo.cookbook.utility.extensions.general.isValidIntegerString
import com.aamo.cookbook.utility.extensions.general.letIf

// TODO: test on device
@Composable
fun IntNumberField(
  value: Int,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  prefix: @Composable (() -> Unit)? = null,
  suffix: @Composable (() -> Unit)? = null,
  supportingText: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
  /** Keeps selection on the right side of zero if the value is zero */
  restrictSelectionOnZero: Boolean = true
) {
  var currentText by rememberSaveable { mutableStateOf("0") }
  var currentSelection by remember { mutableStateOf(TextRange(currentText.length)) }

  val validator = remember { IntFieldValidator() }

  LaunchedEffect(value) {
    // Change text when value changes
    validator.onValid(value = value) { result ->
      if (currentText != result) {
        currentText = result
        currentSelection = TextRange(currentText.length)
      }
    }
  }

  TextField(
    value = TextFieldValue(text = currentText, selection = currentSelection),
    shape = shape,
    colors = colors,
    placeholder = placeholder,
    onValueChange = {
      if (currentText == it.text) {
        // Only selection changed
        // Prevent cursor movement if zero was prepended to the value
        currentSelection = it.selection.letIf(currentText == "0" && restrictSelectionOnZero) {
          TextRange(1)
        }
      }
      else {
        validator.onValid(text = it.text) { v, t ->
          if (currentText != t) {
            currentText = it.text
            currentSelection = it.selection.letIf(currentText == "0") { TextRange(1) }
          }
          if (value != v) onValueChange(v)
        }
      }
    },
    suffix = suffix,
    keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    prefix = prefix,
    supportingText = supportingText,
    isError = isError,
    visualTransformation = visualTransformation,
    keyboardActions = keyboardActions,
    maxLines = 1,
    minLines = 1,
    singleLine = true,
    interactionSource = interactionSource,
    modifier = modifier
  )
}

class IntFieldValidator() {
  fun onValid(text: String, onValid: (value: Int, text: String) -> Unit) {
    val result = transformText(text = text) ?: return
    val value = getValueFromText(result) ?: return

    onValid(value, result)
  }

  fun onValid(value: Int, onValid: (text: String) -> Unit) {
    onValid(value.toString())
  }

  private fun getValueFromText(text: String): Int? {
    if (!text.isValidIntegerString()) return null

    return if (text.isEmpty()) 0 else text.toIntOrNull()
  }

  private fun transformText(text: String): String? {
    val result = text.trimStart('0')

    if (getValueFromText(result) == null) return null

    return result.letIf({ it.isEmpty() }) { "0" }
  }
}