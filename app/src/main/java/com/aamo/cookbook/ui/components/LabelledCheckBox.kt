package com.aamo.cookbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun LabelledCheckBox(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit),
  label: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit = {},
) {
  Column(
    modifier = modifier
      .clickable(
        indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
        interactionSource = remember { MutableInteractionSource() },
        onClick = { onCheckedChange(!checked) }
      )
      .padding(horizontal = 4.dp, vertical = 8.dp)
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Checkbox(
        checked = checked,
        onCheckedChange = null
      )
      Spacer(Modifier.size(8.dp))
      Column {
        label()
        content()
      }
    }
  }
}