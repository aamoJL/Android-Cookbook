package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.aamo.cookbook.R

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
            imageVector = Icons.Filled.Star,
            contentDescription = star.toString(),
            tint = color
          )
        } else {
          Icon(
            painter = painterResource(R.drawable.outline_star_outline_24),
            contentDescription = star.toString(),
            tint = color
          )
        }
      }
    }
  }
}