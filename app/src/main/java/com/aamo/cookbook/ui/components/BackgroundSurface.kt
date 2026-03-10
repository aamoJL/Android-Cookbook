package com.aamo.cookbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import com.aamo.cookbook.R

@Composable
fun BackgroundSurface(modifier: Modifier = Modifier, content: @Composable (BoxScope.() -> Unit)) {
  val noiseTexture = ImageBitmap.imageResource(R.drawable.noisy_texture_100x100)
  val backgroundBrush = remember {
    ShaderBrush(
      shader = ImageShader(
        image = noiseTexture, tileModeX = TileMode.Repeated, tileModeY = TileMode.Repeated
      )
    )
  }

  Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
    Box(Modifier.background(brush = backgroundBrush)) {
      content()
    }
  }
}