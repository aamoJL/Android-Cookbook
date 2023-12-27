package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe

@Composable
fun IngredientListItem(
  ingredient: Recipe.Ingredient,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.Bottom,
    modifier = modifier.fillMaxWidth()
  ) {
    Text(
      text = ingredient.amount.toString(),
      textAlign = TextAlign.End,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.width(70.dp)
    )
    Text(
      text = ingredient.unit,
      fontStyle = FontStyle.Italic,
      modifier = Modifier.width(30.dp)
    )
    Text(
      text = ingredient.name,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.weight(1f, true)
    )
  }
}