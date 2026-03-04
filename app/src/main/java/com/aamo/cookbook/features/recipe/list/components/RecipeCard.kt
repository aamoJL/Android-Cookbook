package com.aamo.cookbook.features.recipe.list.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun RecipeCard(
  recipe: Recipe,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  isBookmarked: Boolean = false,
  rating: Int? = 0
) {
  ElevatedCard(
    shape = RoundedCornerShape(10.dp),
    modifier = modifier.then(Modifier.clickable(onClick = onClick))
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box(
        modifier = Modifier
          .weight(1f, true)
          .fillMaxSize()
      ) {
        Thumbnail(fileName = recipe.thumbnailUri, modifier = Modifier.fillMaxSize())
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(4.dp)
            .fillMaxWidth()
        ) {
          if (isBookmarked) {
            BookmarkIcon(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
                .padding(2.dp)
            )
          }
          if (rating?.let { it > 0 } == true) {
            StarRating(
              rating = rating,
              modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp))
                .padding(2.dp)
            )
          }
        }
      }
      Surface(
        color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = recipe.name,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(4.dp)
        )
      }
    }
  }
}

@Composable
private fun Thumbnail(fileName: String, modifier: Modifier = Modifier) {
  Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceColorAtElevation(0.dp)) {
    if (fileName.isNotEmpty()) {
      Image(
        painter = rememberAsyncImagePainter(
          model = IOService(LocalContext.current).getExternalFileUri(
            Environment.DIRECTORY_PICTURES, fileName
          )
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }
    else {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          painter = painterResource(R.drawable.baseline_no_photography_24),
          tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f),
          contentDescription = null,
        )
      }
    }
  }
}

@Composable
private fun BookmarkIcon(modifier: Modifier = Modifier) {
  Box(modifier) {
    Icon(
      painter = painterResource(R.drawable.bookmark_24px),
      contentDescription = null,
      tint = MaterialTheme.colorScheme.secondaryContainer,
      modifier = Modifier.size(16.dp)
    )
  }
}

@Composable
private fun StarRating(rating: Int, modifier: Modifier = Modifier) {
  val stars = rating.coerceIn(minimumValue = 0, maximumValue = 5)

  Row(modifier = modifier) {
    repeat(stars) {
      Icon(
        painter = painterResource(R.drawable.round_star_rate_24),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(16.dp)
      )
    }
    repeat(5 - stars) {
      Box(modifier = Modifier) {
        Icon(
          painter = painterResource(R.drawable.round_star_rate_24),
          contentDescription = null,
          tint = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .2f),
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeCard(
      recipe = Recipe(name = "Recipe 1"),
      isBookmarked = true,
      rating = 3,
      onClick = { },
      modifier = Modifier.size(150.dp)
    )
  }
}