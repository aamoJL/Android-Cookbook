package com.aamo.cookbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun CountInput(
  value: Int,
  modifier: Modifier = Modifier,
  label: String = "",
  onValueChange: (Int) -> Unit,
  minValue: Int = Int.MIN_VALUE,
  maxValue: Int = Int.MAX_VALUE,
) {
  val decreaseContainerColor = when {
    value > minValue -> MaterialTheme.colorScheme.primaryContainer
    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
  }
  val increaseContainerColor = when {
    value < maxValue -> MaterialTheme.colorScheme.primaryContainer
    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
  }

  Surface {
    Column(modifier = modifier) {
      if (label.isNotEmpty()) Text(text = label, style = MaterialTheme.typography.labelMedium)
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = decreaseContainerColor,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
            .background(decreaseContainerColor)
            .clickable(
              onClick = { onValueChange(value - 1) },
              enabled = value > minValue
            )
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              painter = painterResource(id = R.drawable.baseline_remove_24),
              contentDescription = stringResource(R.string.description_decrease_value),
            )
          }
        }
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
        ) {
          Text(text = value.toString())
        }
        Surface(
          color = increaseContainerColor,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
            .clickable(
              onClick = { onValueChange(value + 1) },
              enabled = value < maxValue
            )
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = stringResource(R.string.description_increase_value),
            )
          }
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    CountInput(value = 10, onValueChange = {})
  }
}