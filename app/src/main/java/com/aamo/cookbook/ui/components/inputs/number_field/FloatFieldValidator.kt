package com.aamo.cookbook.ui.components.inputs.number_field

import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.isValidDecimalNumberString
import com.aamo.cookbook.utility.extensions.general.letIf

data object FloatFieldValidator : FieldValidator<Float> {
  override fun onValid(text: String, onValid: (value: Float, text: String) -> Unit) {
    val result = transformText(text = text) ?: return
    val value = getValueFromText(result) ?: return

    onValid(value, result)
  }

  override fun onValid(
    value: Float, onValid: (text: String) -> Unit
  ): Boolean {
    return if (!value.isFinite()) false
    else {
      onValid(getTextFromValue(value))
      true
    }
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