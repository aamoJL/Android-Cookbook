package com.aamo.cookbook.features.recipe.view.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.inputs.FiveStarRating
import com.aamo.cookbook.utility.extensions.general.EMPTY

@Composable
fun RecipeSettingsScreen(
  ratingOutOfFive: Int,
  thumbnailUri: String,
  onRatingChange: (Int) -> Unit,
  onThumbnailChange: (Uri) -> Unit
) {
  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
    ) {
      Spacer(modifier = Modifier.height(100.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        ThumbnailPicker(
          fileName = thumbnailUri,
          onThumbnailChange = onThumbnailChange,
          modifier = Modifier.size(200.dp)
        )
      }
      Spacer(modifier = Modifier.height(100.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        StarRating(rating = ratingOutOfFive, onRatingChange = onRatingChange)
      }
    }
  }
}

@Composable
private fun ThumbnailPicker(
  fileName: String, onThumbnailChange: (Uri) -> Unit, modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(modifier = modifier) {
    if (fileName.isNotEmpty()) {
      Box(modifier = Modifier.fillMaxSize()) {
        Image(
          painter = rememberAsyncImagePainter(model = PhotoService(context).get(fileName)),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        IconButton(
          onClick = { onThumbnailChange(Uri.EMPTY) }, colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .8f),
          ), modifier = Modifier.align(Alignment.BottomEnd)
        ) {
          Icon(
            painter = painterResource(R.drawable.rounded_delete_24),
            contentDescription = stringResource(R.string.description_delete_photo),
          )
        }
      }
    }
    else {
      Box(modifier = Modifier.fillMaxSize()) {
        CameraButton(
          onCapture = { onThumbnailChange(it) },
          modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
        )
      }
    }
  }
}

@Composable
private fun StarRating(
  rating: Int, onRatingChange: (Int) -> Unit
) {
  Card {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      Text(text = stringResource(R.string.text_rate_the_recipe))
      FiveStarRating(value = rating, onValueChange = onRatingChange)
    }
  }
}

@Composable
private fun CameraButton(
  onCapture: (Uri) -> Unit, modifier: Modifier = Modifier
) {
  var fileUri by remember { mutableStateOf(Uri.EMPTY) }
  val context = LocalContext.current

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(), onResult = { success ->
      if (success) onCapture(fileUri)
    })

  IconButton(
    onClick = {
      runCatching {
        fileUri = PhotoService(context = context).getTempFileUri()
        cameraLauncher.launch(fileUri)
      }
    }, modifier = modifier
  ) {
    Icon(
      painter = painterResource(R.drawable.baseline_add_a_photo_24),
      contentDescription = stringResource(R.string.description_take_a_photo)
    )
  }
}

@Preview
@Composable
private fun Preview() {
  RecipeSettingsScreen(
    ratingOutOfFive = 3,
    thumbnailUri = String.EMPTY,
    onRatingChange = {},
    onThumbnailChange = {})
}