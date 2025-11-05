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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.isNumber
import com.aamo.cookbook.utility.extensions.general.letIf

@Composable
fun NullableFloatNumberField(
  value: Float?,
  onValueChange: (Float?) -> Unit,
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
  val fieldValue = remember { NullableFloatFieldValue(zeroEqualsNull = zeroEqualsNull) }
  var selection by remember { mutableStateOf(TextRange(fieldValue.text.length)) }

  LaunchedEffect(value) {
    // change text if the value was changed outside this component
    if (fieldValue.update(value)) selection = TextRange(fieldValue.text.length)
  }

  TextField(
    value = TextFieldValue(text = fieldValue.text, selection = selection),
    shape = shape,
    colors = colors,
    placeholder = placeholder,
    onValueChange = {
      if (fieldValue.text == it.text) {
        // Only selection changed
        // Prevent cursor movement if zero was prepended to the value
        selection = if (it.text == "0" && restrictSelectionOnZero) {
          TextRange(1)
        }
        else it.selection
      }
      else if (fieldValue.update(it.text)) {
        selection = it.selection.letIf(fieldValue.text == "0") { TextRange(1) }
        onValueChange(fieldValue.value)
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

class NullableFloatFieldValue(val zeroEqualsNull: Boolean = false) {
  var text by mutableStateOf(String.EMPTY)
    private set

  var value by mutableStateOf(getValueFromText(text, zeroEqualsNull))
    private set

  fun update(text: String): Boolean {
    val newText = transformText(text)

    if (newText == null) return false
    if (this.text == newText) return false

    val newValue = getValueFromText(newText, zeroEqualsNull)

    this.text = newText
    this.value = newValue

    return true
  }

  fun update(value: Float?): Boolean {
    val newValue = transformValue(value)

    if (this.value == newValue) return false

    val newText = getTextFromValue(newValue)

    this.value = newValue
    this.text = newText

    return true
  }

  private fun getValueFromText(text: String, zeroEqualsNull: Boolean): Float? {
    if (!text.isNumber()) return null

    val value = when (text) {
      String.EMPTY -> null
      "." -> .0f
      else -> text.toFloatOrNull()
    }

    if (value == null) return null
    if (!value.isFinite()) return null
    if (value == 0f && zeroEqualsNull) return null

    return value
  }

  private fun getTextFromValue(value: Float?): String {
    if (value == 0f && zeroEqualsNull) return String.EMPTY

    return value?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: String.EMPTY
  }

  private fun transformText(text: String): String? {
    if (text.isEmpty()) return String.EMPTY
    if (!text.isNumber()) return null
    if (getValueFromText(text, zeroEqualsNull = false) == null) return null

    var newText = text

    if (newText.startsWith("0")) newText = newText.trimStart('0')
      .letIf({ b -> b.startsWith(".") }) { c -> "0".plus(c) } // Trim leading zeroes, except one
    if (newText.isEmpty()) newText = "0"
    if (newText == "0" && zeroEqualsNull) newText = String.EMPTY

    return newText
  }

  private fun transformValue(value: Float?): Float? {
    if (value?.isFinite() != true) return null
    if (value == 0f && zeroEqualsNull) return null
    return value
  }
}