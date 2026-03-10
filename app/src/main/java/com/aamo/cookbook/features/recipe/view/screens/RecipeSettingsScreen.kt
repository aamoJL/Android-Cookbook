package com.aamo.cookbook.features.recipe.view.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.inputs.FiveStarRating
import com.aamo.cookbook.ui.components.modals.DeleteDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY

@Composable
fun RecipeSettingsScreen(
  ratingOutOfFive: Int,
  thumbnailUri: String,
  onRatingChange: (Int) -> Unit,
  onThumbnailChange: (Uri) -> Unit
) {
  val scrollState = rememberScrollState()

  BackgroundSurface(modifier = Modifier.fillMaxSize()) {
    Column(
      verticalArrangement = Arrangement.spacedBy(32.dp),
      modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxHeight()
        .padding(vertical = 8.dp, horizontal = 32.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.widthIn(max = 400.dp)
        ) {
          HorizontalDividerLabel(label = stringResource(R.string.label_thumbnail))
          ThumbnailPicker(
            fileName = thumbnailUri,
            onThumbnailChange = onThumbnailChange,
            modifier = Modifier.size(200.dp)
          )
        }
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.widthIn(max = 400.dp)
        ) {
          HorizontalDividerLabel(label = stringResource(R.string.label_rating))
          StarRating(rating = ratingOutOfFive, onRatingChange = onRatingChange)
        }
      }
    }
  }
}

@Composable
private fun ThumbnailPicker(
  fileName: String, onThumbnailChange: (Uri) -> Unit, modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_thumbnail),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onThumbnailChange(Uri.EMPTY)
    })

  ElevatedCard(
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier
  ) {
    if (fileName.isNotEmpty()) {
      Box(modifier = Modifier.fillMaxSize()) {
        Image(
          painter = rememberAsyncImagePainter(model = PhotoService(context).get(fileName)),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        IconButton(
          onClick = { openDeleteDialog = true },
          colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .8f),
          ),
          modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
        ) {
          Icon(
            painter = painterResource(R.drawable.rounded_delete_24),
            contentDescription = stringResource(R.string.cd_delete_photo),
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
  ElevatedCard(colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
    FiveStarRating(
      value = rating, onValueChange = onRatingChange, modifier = Modifier.padding(8.dp)
    )
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
        fileUri = PhotoService(context = context).getTemp()
        cameraLauncher.launch(fileUri)
      }
    }, modifier = modifier
  ) {
    Icon(
      painter = painterResource(R.drawable.baseline_add_a_photo_24),
      contentDescription = stringResource(R.string.cd_take_a_photo)
    )
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeSettingsScreen(
      ratingOutOfFive = 3,
      thumbnailUri = String.EMPTY,
      onRatingChange = {},
      onThumbnailChange = {})
  }
}