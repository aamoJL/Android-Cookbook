package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun NoteCard(
  text: String,
  modifier: Modifier = Modifier,
  label: String = "${stringResource(R.string.label_note)}:",
  colors: CardColors = CardDefaults.elevatedCardColors(
    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
  ),
) {
  ElevatedCard(
    colors = colors,
    shape = CutCornerShape(bottomEnd = 15.dp),
    modifier = modifier
      .height(intrinsicSize = IntrinsicSize.Max)
      .width(intrinsicSize = IntrinsicSize.Max)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(modifier = Modifier.padding(8.dp)) {
        if (label.isNotEmpty()) {
          Text(
            text = label,
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.labelSmall,
          )
        }
        Text(text = text, fontStyle = FontStyle.Italic, style = MaterialTheme.typography.bodySmall)
      }
      // Folded corner
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .size(15.dp)
          .clip(RoundedCornerShape(topStart = 4.dp))
          .background(LocalContentColor.current.copy(alpha = .4f))
      )
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    NoteCard(text = "Lorem ipsum")
  }
}