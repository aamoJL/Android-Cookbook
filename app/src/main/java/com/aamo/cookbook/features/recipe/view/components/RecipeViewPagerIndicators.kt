package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme

enum class RecipeViewPagerIndicatorsTags {
  SUMMARY_INDICATOR,
  CHAPTER_INDICATOR
}

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
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      PageIndicatorItem(
        selected = pageIndex == 0,
        onClick = { onPageChange(0) },
        contentDescription = stringResource(R.string.cd_summary_page),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        icon = painterResource(R.drawable.rounded_info_24),
        modifier = Modifier.testTag(RecipeViewPagerIndicatorsTags.SUMMARY_INDICATOR.name)
      )

      repeat(recipeProgress.size) { chapterIndicatorIndex ->
        val completed = recipeProgress[chapterIndicatorIndex]

        PageIndicatorItem(
          selected = pageIndex == chapterIndicatorIndex + 1,
          onClick = { onPageChange(chapterIndicatorIndex + 1) },
          isTargetPage = currentChapterIndex == chapterIndicatorIndex,
          contentDescription = stringResource(
            R.string.cd_chapter_x_page, chapterIndicatorIndex + 1
          ),
          color = when (currentChapterIndex) {
            chapterIndicatorIndex -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerHighest
          },
          icon = if (completed) painterResource(R.drawable.rounded_check_24) else null,
          modifier = Modifier.testTag(RecipeViewPagerIndicatorsTags.CHAPTER_INDICATOR.name)
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
    shape = CircleShape,
    border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
    modifier = modifier
      .padding(10.dp)
      .semantics { this.contentDescription = contentDescription }
      .size(width = if (isTargetPage) 48.dp else 32.dp, height = 32.dp)) {
    if (icon != null) {
      Icon(
        painter = icon, contentDescription = null, // Description will be on the surface element
        modifier = Modifier.padding(4.dp)
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun PagerPreview(
  @PreviewParameter(PageIndexPreviewParameterProvider::class) pageIndex: Int
) {
  CookbookTheme {
    RecipeViewPagerIndicators(
      pageIndex = pageIndex, recipeProgress = listOf(true, false, false), onPageChange = { })
  }
}

private class PageIndexPreviewParameterProvider : PreviewParameterProvider<Int> {
  override val values = sequenceOf(0, 1, 2, 3, 4)
}