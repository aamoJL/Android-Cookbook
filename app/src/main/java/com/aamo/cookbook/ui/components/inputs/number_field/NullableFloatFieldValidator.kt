package com.aamo.cookbook.ui.components.inputs.number_field

import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.isValidDecimalNumberString
import com.aamo.cookbook.utility.extensions.general.letIf

data object NullableFloatFieldValidator : FieldValidator<Float?> {
  override fun onValid(text: String, onValid: (value: Float?, text: String) -> Unit) {
    val result = transformText(text = text) ?: return

    getValueFromText(text = result) { value ->
      onValid(value, result)
    }
  }

  override fun onValid(value: Float?, onValid: (text: String) -> Unit): Boolean {
    return if (value?.isFinite() == false) false
    else {
      onValid(getTextFromValue(value = value))
      true
    }
  }

  private fun getTextFromValue(value: Float?): String {
    return value?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: String.EMPTY
  }

  private fun getValueFromText(text: String, onValid: ((Float?) -> Unit) = {}): Boolean {
    if (!text.isValidDecimalNumberString()) return false

    val value = when (text) {
      String.EMPTY -> null
      "." -> null
      "-" -> null
      "-." -> null
      else -> text.toFloatOrNull() ?: return false
    }

    if (value?.isFinite() == false) return false

    onValid(value)
    return true
  }

  private fun transformText(text: String): String? {
    if (text.isEmpty()) return String.EMPTY

    // zeroes needs to be trimmed so the value will be valid when the text is "0-"
    //    leading zero will be left, if the value is a decimal
    val result = text.letIf({ a -> a.startsWith('0') }) { a ->
      a.trimStart('0').letIf({ b -> b.startsWith(".") }) { c -> "0".plus(c) }
    }

    if (!getValueFromText(result)) return null // invalid value

    return result.letIf({ it.isEmpty() }) { "0" }
  }
}