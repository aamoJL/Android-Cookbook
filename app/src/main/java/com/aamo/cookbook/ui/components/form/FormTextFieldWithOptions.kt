package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextFieldWithOptions(
  value: String,
  label: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = FormTextFieldDefaults.keyboardOptions,
  options: List<String> = emptyList(),
) {
  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded },
    modifier = modifier,
  ) {
    FormTextField(
      // The `menuAnchor` modifier must be passed to the text field for correctness.
      modifier = Modifier.menuAnchor(),
      value = value,
      onValueChange = onValueChange,
      label = label,
      keyboardOptions = keyboardOptions,
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    // filter options based on text field value
    val filteringOptions = options.filter { it.contains(value, ignoreCase = true) }
    if (filteringOptions.isNotEmpty()) {
      DropdownMenu(
        modifier = Modifier
          .background(Color.White)
          .exposedDropdownSize(true),
        properties = PopupProperties(focusable = false),
        expanded = expanded,
        onDismissRequest = { expanded = false },
      ) {
        filteringOptions.forEach { selectionOption ->
          DropdownMenuItem(
            text = { Text(selectionOption) },
            onClick = {
              onValueChange(selectionOption)
              expanded = false
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
          )
        }
      }
    }
  }
}