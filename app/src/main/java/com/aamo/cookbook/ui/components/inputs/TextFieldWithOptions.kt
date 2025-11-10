package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

// TODO: unit test
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsTextField(
  value: String,
  onValueChange: (String) -> Unit,
  options: List<String>,
  modifier: Modifier = Modifier,
  label: @Composable (() -> Unit)? = null,
  shape: Shape = TextFieldDefaults.shape,
  colors: TextFieldColors = TextFieldDefaults.colors(),
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier
  ) {
    TextField(
      value = value,
      onValueChange = onValueChange,
      shape = shape,
      colors = colors,
      label = label,
      trailingIcon = { TrailingIcon(expanded = expanded) },
      keyboardOptions = keyboardOptions,
      modifier = Modifier
        .menuAnchor(
          type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = options.isNotEmpty()
        )
        .fillMaxWidth()
    )
    ExposedDropdownMenu(
      expanded = expanded, onDismissRequest = { expanded = false }) {
      options.forEach { option ->
        Surface(color = MaterialTheme.colorScheme.surface) {
          DropdownMenuItem(
            text = { Text(option) }, onClick = {
            onValueChange(option)
            expanded = false
          }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
          )
        }
      }
    }
  }
}