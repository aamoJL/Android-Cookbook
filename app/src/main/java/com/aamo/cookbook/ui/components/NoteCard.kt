package com.aamo.cookbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.Handwritten

@Composable
fun NoteCard(
  text: String,
  modifier: Modifier = Modifier,
  colors: CardColors = CardDefaults.elevatedCardColors(
    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
  ),
) {
  if(text.isEmpty()) return
  ElevatedCard(
    colors = colors,
    shape = CutCornerShape(bottomEnd = 15.dp)
  ) {
    Box(modifier = modifier) {
      Column(modifier = Modifier.padding(8.dp)) {
        Text(
          text = "${stringResource(R.string.textfield_label_note)}:",
          fontFamily = Handwritten,
          style = MaterialTheme.typography.labelSmall
        )
        Text(text = text, fontFamily = Handwritten, style = MaterialTheme.typography.bodyMedium)
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