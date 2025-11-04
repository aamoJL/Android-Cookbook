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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.aamo.cookbook.utility.extensions.general.letIf
import java.util.regex.Pattern

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
  val fieldValue = remember { FloatFieldValue() }
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
        if (!it.text.startsWith('0')) {
          selection = it.selection.letIf(restrictSelectionOnZero && it.text == "0") { TextRange(1) }
        }
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

class FloatFieldValue() {
  var text by mutableStateOf("0")
    private set

  var value by mutableFloatStateOf(getValueOrNull(text) ?: 0f)
    private set

  fun update(text: String): Boolean {
    val newText = getTextOrNull(text)

    if (this.text == newText) return false

    if (newText == null) return false

    val newValue = getValueOrNull(newText)

    if (newValue == null) return false

    this.text = newText
    this.value = newValue

    return true
  }

  fun update(value: Float): Boolean {
    if (!value.isFinite()) return false
    if (this.value == value) return false

    this.value = value
    this.text = getText(value)

    return true
  }

  /**
   * @return [text] as Float or null. Empty string and "." will return 0.0f.
   */
  private fun getValueOrNull(text: String): Float? {
    @Suppress("HardCodedStringLiteral") val isDigitsOnly = Pattern.matches("^-?\\d*\\.?\\d*", text)

    if (!isDigitsOnly) return null

    return when (text) {
      String.EMPTY -> 0f
      "." -> .0f
      else -> text.toFloatOrNull()
    }?.letIf(condition = { !it.isFinite() }) {
      return null
    }
  }

  private fun getTextOrNull(text: String): String? {
    if (getValueOrNull(text) == null) return null

    return text.letIf({ it.startsWith("0") }) { a ->
      // Trim leading zeroes, except one
      a.trimStart('0').letIf({ b -> b.startsWith(".") }) { c -> "0".plus(c) }
    }.letIf({ it.isEmpty() }) { "0" }
  }

  private fun getText(value: Float): String {
    return value.toBigDecimal().stripTrailingZeros().toPlainString()
  }
}