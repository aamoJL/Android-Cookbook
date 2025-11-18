package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString

@Composable
fun IngredientList(
  ingredients: List<Ingredient>,
  servingsMultiplier: Double,
  modifier: Modifier = Modifier,
  fontFamily: androidx.compose.ui.text.font.FontFamily = Handwritten,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
  Row(modifier = modifier) {
    if (ingredients.any { it.amount != Double.Zero }) {
      Column(modifier = Modifier.width(IntrinsicSize.Max)) {
        ingredients.forEach {
          Text(
            text = if (it.amount == Double.Zero) "" else (it.amount * servingsMultiplier).toFractionFormattedString(),
            style = textStyle,
            fontFamily = fontFamily,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
    if (ingredients.any { it.unit.isNotEmpty() }) {
      Column(
        modifier = Modifier
          .defaultMinSize(minWidth = 40.dp)
          .padding(horizontal = 8.dp)
      ) {
        ingredients.forEach { Text(text = it.unit, style = textStyle, fontFamily = fontFamily) }
      }
    }
    Column {
      ingredients.forEach { Text(text = it.name, style = textStyle, fontFamily = fontFamily) }
    }
  }
}