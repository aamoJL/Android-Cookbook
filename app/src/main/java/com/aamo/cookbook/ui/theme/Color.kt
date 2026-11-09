package com.aamo.cookbook.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.BackgroundSurface

val md_theme_light_primary = Color(0xFFFFA98C)
val md_theme_light_onPrimary = Color(0xFF3B170C)
val md_theme_light_primaryContainer = Color(0xFFFFCFBF)
val md_theme_light_onPrimaryContainer = Color(0xFF332824)
val md_theme_light_secondary = Color(0xFF77574B)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFE2D9)
val md_theme_light_onSecondaryContainer = Color(0xFF4D2113)
val md_theme_light_tertiary = Color(0xFFBF8000)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFFFDDAF)
val md_theme_light_onTertiaryContainer = Color(0xFF281800)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFC6BF)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_surfaceVariant = Color(0xFFBFAEAE)
val md_theme_light_onSurfaceVariant = Color(0xFF53433E)
val md_theme_light_outline = Color(0xFF665A57)
val md_theme_light_inverseOnSurface = Color(0xFFFBEEEA)
val md_theme_light_inverseSurface = Color(0xFF362F2C)
val md_theme_light_inversePrimary = Color(0xFFD97F62)
val md_theme_light_surfaceTint = Color(0xFFFFFFFF)
val md_theme_light_outlineVariant = Color(0xFFCCC4C2)
val md_theme_light_scrim = Color(0xFF000000)
val md_theme_light_background = Color(0xFFFAECE8)
val md_theme_light_onBackground = Color(0xFF201A18)
val md_theme_light_surface = Color(0xFFFCF2F0)
val md_theme_light_onSurface = Color(0xFF4D4846)
val md_theme_light_surfaceContainerLowest = Color(0xFFD9C0BA)
val md_theme_light_surfaceContainerLow = Color(0xFFFCEBE6)
val md_theme_light_surfaceContainer = Color(0xFFFFF2F0)
val md_theme_light_surfaceContainerHigh = Color(0xFFFFFAF7)
val md_theme_light_surfaceContainerHighest = Color(0xFFFFFFFF)
val md_theme_light_noteContainer = Color(0xFFFFDDAF)
val md_theme_light_onNoteContainer = Color(0xFF281800)

val md_theme_dark_primary = Color(0xFFD9876C)
val md_theme_dark_onPrimary = Color(0xFF260E08)
val md_theme_dark_primaryContainer = Color(0xFF995E4D)
val md_theme_dark_onPrimaryContainer = Color(0xFFF5E3DC)
val md_theme_dark_secondary = Color(0xFFE7BEAE)
val md_theme_dark_onSecondary = Color(0xFF442A20)
val md_theme_dark_secondaryContainer = Color(0xFF664438)
val md_theme_dark_onSecondaryContainer = Color(0xFFE7BDAF)
val md_theme_dark_tertiary = Color(0xFFFDBB4A)
val md_theme_dark_onTertiary = Color(0xFF432C00)
val md_theme_dark_tertiaryContainer = Color(0xFF664C1F)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFDDAF)
val md_theme_dark_error = Color(0xFFCC6666)
val md_theme_dark_errorContainer = Color(0xFF73282C)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_surfaceVariant = Color(0xFF302B2B)
val md_theme_dark_onSurfaceVariant = Color(0xFFE6CCC3)
val md_theme_dark_outline = Color(0xFFBFA8A1)
val md_theme_dark_inverseOnSurface = Color(0xFF201A18)
val md_theme_dark_inverseSurface = Color(0xFFEDE0DC)
val md_theme_dark_inversePrimary = Color(0xFFB37D6B)
val md_theme_dark_surfaceTint = Color(0xFF807B79)
val md_theme_dark_outlineVariant = Color(0xFF53433E)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_background = Color(0xFF0F0D0D)
val md_theme_dark_onBackground = Color(0xFFEDE0DC)
val md_theme_dark_surface = Color(0xFF211E1E)
val md_theme_dark_onSurface = Color(0xFFCCC2BE)
val md_theme_dark_surfaceContainerLowest = Color(0xFF0F0E0E)
val md_theme_dark_surfaceContainerLow = Color(0xFF1C1919)
val md_theme_dark_surfaceContainer = Color(0xFF211E1E)
val md_theme_dark_surfaceContainerHigh = Color(0xFF292424)
val md_theme_dark_surfaceContainerHighest = Color(0xFF302B2B)
val md_theme_dark_noteContainer = Color(0xFF664C1F)
val md_theme_dark_onNoteContainer = Color(0xFFFFDDAF)

@PreviewLightDark
@Composable
private fun SurfacePreview() {
  CookbookTheme {
    BackgroundSurface(modifier = Modifier.fillMaxSize()) {
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        Surface(
          shape = MaterialTheme.shapes.medium,
          color = MaterialTheme.colorScheme.surfaceContainer,
          modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
        ) {}
        Surface(
          shape = MaterialTheme.shapes.medium,
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          modifier = Modifier.fillMaxSize(),
        ) {
          Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
              .padding(16.dp)
              .fillMaxSize(),
          ) {
            Surface(
              shape = MaterialTheme.shapes.medium,
              color = MaterialTheme.colorScheme.surfaceContainer,
              modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            ) {
              Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                  .padding(16.dp)
                  .fillMaxSize(),
              ) {
                Surface(
                  shape = MaterialTheme.shapes.medium,
                  color = MaterialTheme.colorScheme.surfaceContainerHighest,
                  modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                ) {}
              }
            }
          }
        }
      }
    }
  }
}