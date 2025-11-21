package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R

@Composable
fun FiveStarRating(
  value: Int?,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  val value = value?.coerceIn(0, 5) ?: 0

  Row(modifier = modifier) {
    repeat(value) {
      val starValue = it + 1
      IconButton(
        onClick = { onValueChange(starValue) }, modifier = Modifier.size(48.dp)
      ) {
        Icon(
          painter = painterResource(R.drawable.round_star_rate_24),
          contentDescription = stringResource(
            R.string.cd_star_rating_star_icon_selected, starValue
          ),
          tint = color,
          modifier = Modifier.fillMaxSize()
        )
      }
    }

    repeat(5 - value) {
      val starValue = value + it + 1
      IconButton(onClick = { onValueChange(starValue) }, Modifier.size(48.dp)) {
        Icon(
          painter = painterResource(R.drawable.round_star_outline_24),
          contentDescription = stringResource(
            R.string.cd_star_rating_star_icon_unselected, starValue
          ),
          tint = color,
          modifier = Modifier.fillMaxSize(.8f)
        )
      }
    }
  }
}

@Preview
@Composable
private fun Preview() {
  FiveStarRating(value = 3, onValueChange = {})
}