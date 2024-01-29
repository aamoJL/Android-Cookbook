package com.aamo.cookbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.theme.Handwritten

@Composable
fun RecipeCard(
  recipe: Recipe,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isFavorite: Boolean = false
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
        // TODO: Image
        if (isFavorite) FavoriteIcon(modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(vertical = 8.dp, horizontal = 12.dp)
        )
      }
      Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
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
private fun FavoriteIcon(modifier: Modifier = Modifier) {
  Box(modifier) {
    Icon(
      imageVector = Icons.Outlined.Favorite,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primaryContainer,
    )
    Icon(
      imageVector = Icons.Outlined.FavoriteBorder,
      contentDescription = null,
    )
  }
}