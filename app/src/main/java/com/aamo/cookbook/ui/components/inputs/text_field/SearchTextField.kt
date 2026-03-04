package com.aamo.cookbook.ui.components.inputs.text_field

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY

@Composable
fun SearchTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  placeholder: String = String.EMPTY,
  shape: Shape = RoundedCornerShape(4.dp),
) {
  TextField(
    value = value,
    onValueChange = onValueChange,
    placeholder = { Text(placeholder) },
    leadingIcon = {
      Icon(painter = painterResource(R.drawable.rounded_search_24), contentDescription = null)
    },
    trailingIcon = {
      if (value.isNotEmpty()) {
        IconButton(onClick = { onValueChange(String.EMPTY) }) {
          Icon(
            painter = painterResource(R.drawable.round_clear_24),
            contentDescription = stringResource(R.string.cd_clear)
          )
        }
      }
    },
    singleLine = true,
    shape = shape,
    colors = TextFieldDefaults.colors(
      focusedIndicatorColor = Color.Transparent,
      errorIndicatorColor = Color.Transparent,
      disabledIndicatorColor = Color.Transparent,
      unfocusedIndicatorColor = Color.Transparent,
      focusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedContainerColor = MaterialTheme.colorScheme.surface,
      unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
      focusedTextColor = MaterialTheme.colorScheme.onSurface,
      cursorColor = MaterialTheme.colorScheme.inversePrimary,
    ),
    modifier = modifier
  )
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    Surface {
      SearchTextField(value = "search text", placeholder = String.EMPTY, onValueChange = {})
    }
  }
}