package com.aamo.cookbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.theme.Handwritten
import kotlin.math.max

@Composable
fun RecipeCard(
  recipe: Recipe,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isFavorite: Boolean = false,
  rating: Int = 0
) {
  ElevatedCard(
    shape = RectangleShape,
    modifier = modifier
      .clickable(onClick = onClick)
      .height(200.dp)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(modifier = Modifier
        .weight(1f, true)
        .fillMaxSize()) {
        if (isFavorite) {
          FavoriteIcon(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(vertical = 8.dp, horizontal = 8.dp)
          )
        }
        if (rating != 0) {
          StarRating(
            rating = rating,
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(vertical = 4.dp)
          )
        }
      }
      Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(4.dp)
        ) {
          Text(
            text = recipe.name,
            fontFamily = Handwritten,
            style = MaterialTheme.typography.titleMedium
          )
        }
      }
    }
  }
}

@Composable
private fun FavoriteIcon(
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  Box(modifier) {
    Icon(
      imageVector = Icons.Outlined.Favorite,
      contentDescription = null,
      tint = color.copy(alpha = .2f),
    )
    Icon(
      imageVector = Icons.Outlined.FavoriteBorder,
      contentDescription = null,
      tint = color,
    )
  }
}

@Composable
private fun StarRating(
  rating: Int,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.primary
) {
  Row(modifier = modifier) {
    repeat(rating) {
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(16.dp)
      )
    }
    repeat(max(0, 5 - rating)) {
      Box(modifier = Modifier){
        Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = null,
          tint = color.copy(alpha = .2f),
          modifier = Modifier.size(16.dp)
        )
        Icon(
          painter = painterResource(R.drawable.outline_star_outline_24),
          contentDescription = null,
          tint = color.copy(alpha = .2f),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}