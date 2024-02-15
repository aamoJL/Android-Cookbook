package com.aamo.cookbook.ui.screen.recipeScreen

import android.content.Context
import android.net.Uri
import android.os.Environment
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.BuildConfig
import com.aamo.cookbook.R
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.components.FiveStarRating
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import java.io.File
import java.util.Objects

@Composable
internal fun CompletedPage(
  uiState: RecipeScreenViewModel.CompletedPageUiState,
  onRatingChange: (Int) -> Unit,
  onThumbnailChange: (Uri) -> Unit,
) {
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
        fileName = uiState.recipeThumbnail,
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
      StarRating(rating = uiState.fiveStarRating, onRatingChange = onRatingChange)
    }
  }
}

@Composable
private fun ThumbnailPicker(
  fileName: String,
  onThumbnailChange: (Uri) -> Unit,
  modifier: Modifier = Modifier
){
  Card(modifier = modifier) {
    if (fileName.isNotEmpty()) {
      Box(modifier = Modifier.fillMaxSize()) {
        Image(
          painter = rememberAsyncImagePainter(
            model = IOService(LocalContext.current)
              .getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
          ),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        IconButton(
          onClick = { onThumbnailChange(Uri.EMPTY) },
          colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .8f),
          ),
          modifier = Modifier.align(Alignment.BottomEnd)
        ) {
          Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(R.string.description_delete_photo),
          )
        }
      }
    } else {
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
  rating: Int,
  onRatingChange: (Int) -> Unit
) {
  Card {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(8.dp)
    ) {
      Text(text = stringResource(R.string.text_rate_the_recipe))
      FiveStarRating(
        value = rating,
        onValueChange = onRatingChange
      )
    }
  }
}

@Composable
private fun CameraButton(
  onCapture: (Uri) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val file = context.createImageFile()
  val uri = FileProvider.getUriForFile(
    Objects.requireNonNull(context),
    "${BuildConfig.APPLICATION_ID}.provider",
    file
  )
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture(),
    onResult = { success ->
      if (success) onCapture(uri)
    }
  )

  IconButton(onClick = { cameraLauncher.launch(uri) }, modifier = modifier) {
    Icon(
      painter = painterResource(R.drawable.baseline_add_a_photo_24),
      contentDescription = stringResource(R.string.description_take_a_photo)
    )
  }
}

private fun Context.createImageFile(): File {
  val storageDir: File? = IOService(this).getExternalFileDir(Environment.DIRECTORY_PICTURES)
  return File.createTempFile(
    System.currentTimeMillis().toString(), /* prefix */
    ".jpg", /* suffix */
    storageDir /* directory */
  )
}