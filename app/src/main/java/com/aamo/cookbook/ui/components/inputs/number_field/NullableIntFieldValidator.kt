package com.aamo.cookbook.ui.components.inputs.number_field

import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.isValidIntegerString
import com.aamo.cookbook.utility.extensions.general.letIf

data object NullableIntFieldValidator : FieldValidator<Int?> {
  override fun onValid(text: String, onValid: (value: Int?, text: String) -> Unit) {
    val result = transformText(text = text) ?: return

    getValueFromText(text = result) { value ->
      onValid(value, result)
    }
  }

  override fun onValid(value: Int?, onValid: (text: String) -> Unit): Boolean {
    onValid(value?.toString() ?: String.EMPTY)
    return true
  }

  private fun getValueFromText(text: String, onValid: ((Int?) -> Unit) = {}): Boolean {
    if (!text.isValidIntegerString()) return false

    val result = when (text) {
      String.EMPTY -> null
      "-" -> null
      else -> text.toIntOrNull() ?: return false
    }

    onValid(result)
    return true
  }

  private fun transformText(text: String): String? {
    if (text.isEmpty()) return String.EMPTY

    val result = if (text.startsWith('0')) {
      // zeroes needs to be trimmed so the value will be valid when the text is "0-",
      //  but if the text is empty after trimming, zero needs to be added so "0" will not be null
      text.trimStart('0').letIf({ it.isEmpty() }) { "0" }
    }
    else text

    if (!getValueFromText(result)) return null // invalid value

    return result
  }
}