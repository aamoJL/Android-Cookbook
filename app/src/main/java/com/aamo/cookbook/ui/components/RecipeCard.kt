package com.aamo.cookbook.ui.components

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.theme.CookbookTheme
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
    modifier = modifier.then(Modifier.clickable(onClick = onClick))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(modifier = Modifier
        .weight(1f, true)
        .fillMaxSize()
      ) {
        Thumbnail(
          fileName = recipe.thumbnailUri,
          modifier = Modifier.fillMaxSize())
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(4.dp)
            .fillMaxWidth()
        ) {
          if (isFavorite) {
            FavoriteIcon(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = .8f))
                .padding(2.dp)
            )
          }
          if (rating != 0) {
            StarRating(
              rating = rating,
              modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = .8f))
                .padding(2.dp)
            )
          }
        }
      }
      Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
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
private fun Thumbnail(
  fileName: String,
  modifier: Modifier = Modifier
) {
  Surface(modifier = modifier) {
    if (fileName.isNotEmpty()) {
      Image(
        painter = rememberAsyncImagePainter(
          model = IOService(LocalContext.current)
            .getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Box {
        Icon(
          painter = painterResource(R.drawable.baseline_no_photography_24),
          tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f),
          contentDescription = null,
          modifier = Modifier.align(Alignment.Center)
        )
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
      tint = MaterialTheme.colorScheme.tertiaryContainer,
      modifier = Modifier.size(16.dp)
    )
    Icon(
      imageVector = Icons.Outlined.FavoriteBorder,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.tertiary,
      modifier = Modifier.size(16.dp)
    )
  }
}

@Composable
private fun StarRating(
  rating: Int,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    repeat(rating) {
      Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(16.dp)
      )
    }
    repeat(max(0, 5 - rating)) {
      Box(modifier = Modifier){
        Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .2f),
          modifier = Modifier.size(16.dp)
        )
        Icon(
          painter = painterResource(R.drawable.outline_star_outline_24),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondary.copy(alpha = .2f),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeCard(
      recipe = Recipe(name = "Recipe 1"),
      isFavorite = true,
      rating = 3,
      onClick = { })
  }
}