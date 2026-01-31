package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString

@Composable
fun IngredientList(
  ingredients: List<Ingredient>,
  servingsMultiplier: Double,
  modifier: Modifier = Modifier,
  fontFamily: FontFamily = FontFamily.Default,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
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
      Column(modifier = Modifier.defaultMinSize(minWidth = 20.dp)) {
        ingredients.forEach { Text(text = it.unit, style = textStyle, fontFamily = fontFamily) }
      }
    }
    Column {
      ingredients.forEach { Text(text = it.name, style = textStyle, fontFamily = fontFamily) }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun Preview(
  @PreviewParameter(UserPreviewParameterProvider::class) ingredients: List<Ingredient>
) {
  IngredientList(
    ingredients = ingredients, servingsMultiplier = 1.0
  )
}

@Suppress("HardCodedStringLiteral")
private class UserPreviewParameterProvider : PreviewParameterProvider<List<Ingredient>> {
  override val values = sequenceOf(
    listOf(
      Ingredient(name = "Ingredient 1", amount = 250.0, unit = "g"),
      Ingredient(name = "Ingredient 2", amount = 2.0, unit = "pieces"),
      Ingredient(name = "Ingredient 3", amount = 25.0, unit = "kpl"),
    ),
    listOf(
      Ingredient(name = "Ingredient 1", amount = 250.0, unit = "g"),
      Ingredient(name = "Ingredient 2", unit = "g"),
      Ingredient(name = "Ingredient 3", amount = 25.0),
    ),
    listOf(
      Ingredient(name = "Ingredient 1", unit = "g"),
      Ingredient(name = "Ingredient 2", unit = "g"),
      Ingredient(name = "Ingredient 3"),
    ),
    listOf(
      Ingredient(name = "Ingredient 1", amount = 100.0),
      Ingredient(name = "Ingredient 2", amount = 2.0),
      Ingredient(name = "Ingredient 3", amount = 19.12),
    ),
  )
}