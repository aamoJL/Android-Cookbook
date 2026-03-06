package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY

enum class CountInputTags {
  TITLE,
  INCREASE,
  DECREASE,
  VALUE
}

@Composable
fun CountInput(
  value: Int,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  label: String = String.EMPTY,
  minValue: Int = Int.MIN_VALUE,
  maxValue: Int = Int.MAX_VALUE,
  color: Color = MaterialTheme.colorScheme.primaryContainer,
  elevation: CardElevation = CardDefaults.elevatedCardElevation()
) {
  ElevatedCard(elevation = elevation, shape = RoundedCornerShape(8.dp)) {
    Column(modifier = modifier) {
      if (label.isNotEmpty()) Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.testTag(CountInputTags.TITLE.name)
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        FilledIconButton(
          onClick = { onValueChange(value - 1) },
          shape = RectangleShape,
          enabled = value > minValue,
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.contentColorFor(color),
          ),
          modifier = Modifier
            .size(40.dp)
            .defaultMinSize()
            .testTag(CountInputTags.DECREASE.name)
        ) {
          Icon(
            painter = painterResource(R.drawable.baseline_remove_24),
            contentDescription = stringResource(R.string.cd_decrease_value)
          )
        }
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 40.dp)
        ) {
          Text(text = value.toString(), modifier = Modifier.testTag(CountInputTags.VALUE.name))
        }
        FilledIconButton(
          onClick = { onValueChange(value + 1) },
          shape = RectangleShape,
          enabled = value < maxValue,
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.contentColorFor(color),
          ),
          modifier = Modifier
            .size(40.dp)
            .defaultMinSize()
            .testTag(CountInputTags.INCREASE.name)
        ) {
          Icon(
            painter = painterResource(R.drawable.rounded_add_24),
            contentDescription = stringResource(R.string.cd_increase_value)
          )
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

@PreviewLightDark
@Composable
private fun DisabledPreview() {
  CookbookTheme {
    CountInput(
      value = 0,
      onValueChange = {},
      maxValue = -1,
      minValue = 1,
    )
  }
}