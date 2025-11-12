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
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.isValidDecimalNumberString
import com.aamo.cookbook.utility.extensions.general.letIf

// TODO: unit test
@Composable
fun FloatNumberField(
  value: Float,
  onValueChange: (Float) -> Unit,
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
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions.Default,
  interactionSource: MutableInteractionSource? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
  /** Keeps selection on the right side of zero if the value is zero */
  restrictSelectionOnZero: Boolean = true
) {
  var currentText by rememberSaveable { mutableStateOf("0") }
  var currentSelection by remember { mutableStateOf(TextRange(currentText.length)) }

  LaunchedEffect(value) {
    if (!value.isFinite()) {
      currentText = value.toString()
      currentSelection = TextRange(currentText.length)
    }
    else {
      // change text when value changes...
      FloatFieldValidator.onValid(value = value) { valueText ->
        // ... but only if the currentText's value is different
        FloatFieldValidator.onValid(text = currentText) { currentTextValue, _ ->
          if (value != currentTextValue) {
            currentText = valueText
            currentSelection = TextRange(currentText.length)
          }
        }
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
        currentSelection = it.selection.letIf(it.text == "0" && restrictSelectionOnZero) {
          TextRange(1)
        }
      }
      else {
        FloatFieldValidator.onValid(text = it.text) { v, t ->
          if (currentText != t) {
            currentText = t
            currentSelection = it.selection.letIf(currentText == "0") { TextRange(1) }
          }
          if (value != v) onValueChange(v)
        }
      }
    },
    suffix = suffix,
    keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
    enabled = enabled,
    readOnly = !value.isFinite() || readOnly,
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

data object FloatFieldValidator {
  fun onValid(text: String, onValid: (value: Float, text: String) -> Unit) {
    val result = transformText(text = text) ?: return
    val value = getValueFromText(result) ?: return

    onValid(value, result)
  }

  fun onValid(value: Float, onValid: (text: String) -> Unit) {
    if (!value.isFinite()) return
    val text = getTextFromValue(value)

    onValid(text)
  }

  private fun getTextFromValue(value: Float): String {
    return value.toBigDecimal().stripTrailingZeros().toPlainString()
  }

  private fun getValueFromText(text: String): Float? {
    if (!text.isValidDecimalNumberString()) return null

    val value = when (text) {
      String.EMPTY -> 0f
      "." -> .0f
      "-" -> 0f
      "-." -> 0f
      else -> text.toFloatOrNull()
    } ?: return null

    if (!value.isFinite()) return null

    return value
  }

  private fun transformText(text: String): String? {
    // zeroes needs to be trimmed so the value will be valid when the text is "0-"
    //    leading zero will be left, if the value is a decimal
    val result = text.letIf({ a -> a.startsWith('0') }) { a ->
      a.trimStart('0').letIf({ b -> b.startsWith(".") }) { c -> "0".plus(c) }
    }

    if (getValueFromText(result) == null) return null

    return result.letIf({ it.isEmpty() }) { "0" }
  }
}