package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R

// TODO: unit test
@Composable
fun FiveStarRating(
  value: Int?,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  Row(modifier = modifier) {
    repeat(5) {
      val star = it + 1
      IconButton(onClick = { onValueChange(star) }) {
        if (value != null && value >= star) {
          Icon(
            painter = painterResource(R.drawable.round_star_rate_24),
            contentDescription = stringResource(R.string.description_star_rating_star_icon, star),
            tint = color
          )
        }
        else {
          Icon(
            painter = painterResource(R.drawable.round_star_outline_24),
            contentDescription = stringResource(R.string.description_star_rating_star_icon, star),
            tint = color
          )
        }
      }
    }
  }
}