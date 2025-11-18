package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeViewPagerIndicators(
  pageIndex: Int,
  recipeProgress: List<Boolean>,
  onPageChange: (page: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentChapterIndex = remember(recipeProgress) { recipeProgress.indexOfFirst { !it } }

  Surface {
    Row(
      modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
      PageIndicatorItem(
        selected = pageIndex == 0,
        onClick = { onPageChange(0) },
        contentDescription = stringResource(R.string.cd_settings_page),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        icon = painterResource(R.drawable.rounded_settings_24)
      )

      PageIndicatorItem(
        selected = pageIndex == 1,
        onClick = { onPageChange(1) },
        contentDescription = stringResource(R.string.cd_summary_page),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        icon = painterResource(R.drawable.rounded_info_24),
      )

      repeat(recipeProgress.size) { chapterIndicatorIndex ->
        PageIndicatorItem(
          selected = pageIndex == chapterIndicatorIndex + 2,
          onClick = { onPageChange(chapterIndicatorIndex + 2) },
          isTargetPage = currentChapterIndex == chapterIndicatorIndex,
          contentDescription = stringResource(
            R.string.cd_chapter_x_page, chapterIndicatorIndex + 1
          ),
          color = when (currentChapterIndex) {
            chapterIndicatorIndex -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
          },
          icon = if (recipeProgress[chapterIndicatorIndex]) painterResource(R.drawable.rounded_check_24) else null
        )
      }
    }
  }
}

@Composable
private fun PageIndicatorItem(
  selected: Boolean,
  onClick: () -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
  isTargetPage: Boolean = false,
  enabled: Boolean = true,
  color: Color = MaterialTheme.colorScheme.secondaryContainer,
  icon: Painter? = null,
) {
  Surface(
    color = color,
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .padding(10.dp)
      .clip(CircleShape)
      .semantics { this.contentDescription = contentDescription }
      .size(
        width = if (isTargetPage) 48.dp else 32.dp, height = 32.dp
      )) {
    Box(contentAlignment = Alignment.Center) {
      if (icon != null) {
        Icon(
          painter = icon,
          contentDescription = null, // Description will be on the surface element
        )
      }
      if (selected) {
        Surface(
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier
            .clip(CircleShape)
            .size(24.dp)
        ) {}
      }
    }
  }
}

@Preview
@Composable
private fun PagerPreview() {
  CookbookTheme(useDarkTheme = true) {
    RecipeViewPagerIndicators(
      pageIndex = 4, recipeProgress = listOf(true, false, false), onPageChange = { })
  }
}