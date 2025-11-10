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
import com.aamo.cookbook.utility.extensions.general.isValidIntegerString
import com.aamo.cookbook.utility.extensions.general.letIf

// TODO: unit test
@Composable
fun NullableIntNumberField(
  value: Int?,
  onValueChange: (Int?) -> Unit,
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
  restrictSelectionOnZero: Boolean = true,
  zeroEqualsNull: Boolean = false
) {
  var currentValue by rememberSaveable { mutableStateOf(value) }
  var currentText by rememberSaveable { mutableStateOf(String.EMPTY) }
  var currentSelection by remember { mutableStateOf(TextRange(currentText.length)) }

  val validator = remember {
    NullableIntFieldValidator(
      zeroEqualsNull = zeroEqualsNull, update = { t, v -> currentText = t; currentValue = v })
  }

  LaunchedEffect(currentValue != value) {
    // change text if the value was changed outside this component
    if (validator.update(new = value, old = currentValue)) {
      currentSelection = TextRange(currentText.length)
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
      else if (validator.update(new = it.text, old = currentText)) {
        currentSelection = it.selection.letIf(currentText == "0") { TextRange(1) }
        onValueChange(currentValue)
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

class NullableIntFieldValidator(
  val zeroEqualsNull: Boolean, private val update: (text: String, value: Int?) -> Unit
) {
  fun update(new: String, old: String): Boolean {
    val newText = transformText(new) ?: return false

    if (old == newText) return false

    val newValue = getValueFromText(newText, zeroEqualsNull)

    update(newText, newValue)

    return true
  }

  fun update(new: Int?, old: Int?): Boolean {
    val newValue = transformValue(new)

    if (old == newValue) return false

    val newText = getTextFromValue(newValue)

    update(newText, newValue)

    return true
  }

  private fun getValueFromText(text: String, zeroEqualsNull: Boolean): Int? {
    if (!text.isValidIntegerString()) return null

    val value = text.toIntOrNull() ?: return null

    if (value == 0 && zeroEqualsNull) return null

    return value
  }

  private fun getTextFromValue(value: Int?): String {
    if (value == 0 && zeroEqualsNull) return String.EMPTY

    return value?.toString() ?: String.EMPTY
  }

  private fun transformText(text: String): String? {
    if (text.isEmpty()) return String.EMPTY
    if (getValueFromText(text, zeroEqualsNull = false) == null) return null

    var newText = text.trimStart('0') // Trim leading zeroes

    if (newText.isEmpty()) newText = "0"
    if (newText == "0" && zeroEqualsNull) newText = String.EMPTY

    return newText
  }

  private fun transformValue(value: Int?): Int? {
    return if (value == 0 && zeroEqualsNull) null else value
  }
}