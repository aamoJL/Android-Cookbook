package com.aamo.cookbook.ui.components.inputs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme

enum class FiveStarRatingTags {
  RATING_STAR
}

@Composable
fun FiveStarRating(
  value: Int?,
  onValueChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.inversePrimary
) {
  val value = value?.coerceIn(0, 5) ?: 0

  Row(modifier = modifier) {
    repeat(5) { index ->
      val starValue = index + 1

      IconButton(
        onClick = { onValueChange(starValue) },
        modifier = Modifier.size(48.dp),
      ) {
        if (value > index) {
          Icon(
            painter = painterResource(R.drawable.round_star_rate_24),
            contentDescription = stringResource(
              R.string.cd_star_rating_star_icon_selected, starValue
            ),
            tint = color,
            modifier = Modifier
              .fillMaxSize()
              .testTag(FiveStarRatingTags.RATING_STAR.name)
          )
        }
        else {
          Icon(
            painter = painterResource(R.drawable.round_star_outline_24),
            contentDescription = stringResource(
              R.string.cd_star_rating_star_icon_unselected, starValue
            ),
            tint = color.copy(alpha = .7f),
            modifier = Modifier
              .fillMaxSize(.8f)
              .testTag(FiveStarRatingTags.RATING_STAR.name)
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
    Surface {
      FiveStarRating(value = 3, onValueChange = {})
    }
  }
}