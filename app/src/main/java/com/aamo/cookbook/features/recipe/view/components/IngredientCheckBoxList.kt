package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.ui.components.inputs.LabelledCheckBox
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientCheckBoxList(
  ingredients: List<Ingredient>,
  ingredientSelection: ViewModelStateList<Long>,
  servingsMultiplier: Double,
  modifier: Modifier = Modifier,
  fontFamily: FontFamily = FontFamily.Default,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  softWrap: Boolean = true,
) {
  Column(modifier = modifier.width(intrinsicSize = IntrinsicSize.Max)) {
    ingredients.forEach {
      val checked = ingredientSelection.values.contains(it.id)

      CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 36.dp) {
        LabelledCheckBox(
          checked = checked,
          onCheckedChange = { value ->
            if (value) ingredientSelection.add(it.id)
            else ingredientSelection.remove(it.id)
          },
          modifier = Modifier.fillMaxWidth(),
        ) {
          CheckboxLabel(
            ingredient = it,
            checked = checked,
            servingsMultiplier = servingsMultiplier,
            fontFamily = fontFamily,
            textStyle = textStyle,
            softWrap = softWrap,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@Composable
private fun CheckboxLabel(
  ingredient: Ingredient,
  modifier: Modifier = Modifier,
  checked: Boolean,
  servingsMultiplier: Double,
  fontFamily: FontFamily = FontFamily.Default,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  softWrap: Boolean = true,
) {
  Box(modifier = modifier) {
    if (checked) {
      HorizontalDivider(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center)
      )
    }
    Row(
      verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
      if (ingredient.amount != Double.Zero) {
        Text(
          text = (ingredient.amount * servingsMultiplier).toFractionFormattedString(),
          style = textStyle,
          fontFamily = fontFamily,
          softWrap = softWrap,
          color = if (checked) MaterialTheme.colorScheme.outlineVariant else Color.Unspecified,
          modifier = Modifier.padding(end = if (ingredient.unit.isNotEmpty()) 2.dp else 8.dp)
        )
      }
      if (ingredient.unit.isNotEmpty()) {
        Text(
          text = ingredient.unit,
          style = textStyle,
          fontFamily = fontFamily,
          softWrap = softWrap,
          color = if (checked) MaterialTheme.colorScheme.outlineVariant else Color.Unspecified,
          modifier = Modifier.padding(end = 8.dp)
        )
      }
      Text(
        text = ingredient.name,
        style = textStyle,
        softWrap = softWrap,
        color = if (checked) MaterialTheme.colorScheme.outlineVariant else Color.Unspecified,
        fontFamily = fontFamily,
      )
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview(showBackground = true)
@Composable
private fun LabelPreview() {
  CookbookTheme {
    Column() {
      CheckboxLabel(
        ingredient = Ingredient(id = 1, name = "Ingredient 1", amount = 250.0, unit = "g"),
        servingsMultiplier = 1.0,
        checked = true,
        softWrap = true
      )
      CheckboxLabel(
        ingredient = Ingredient(id = 1, name = "Ingredient 1", amount = 250.0, unit = "g"),
        servingsMultiplier = 1.0,
        checked = false,
        softWrap = true
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun Preview(
  @PreviewParameter(IngredientCheckBoxListPreviewParameterProvider::class)
  ingredients: List<Ingredient>
) {
  IngredientCheckBoxList(
    ingredients = ingredients,
    servingsMultiplier = 1.0,
    ingredientSelection = ViewModelStateList(listOf(1)),
  )
}

@Suppress("HardCodedStringLiteral")
private class IngredientCheckBoxListPreviewParameterProvider :
        PreviewParameterProvider<List<Ingredient>> {
  override val values = sequenceOf(
    listOf(
      Ingredient(id = 1, name = "Ingredient 1", amount = 250.0, unit = "g"),
      Ingredient(id = 2, name = "Ingredient 2", amount = 2.0, unit = "pieces"),
      Ingredient(id = 3, name = "Ingredient 3", amount = 25.0, unit = "kpl"),
    ),
    listOf(
      Ingredient(id = 1, name = "Ingredient 1", amount = 250.0, unit = "g"),
      Ingredient(id = 2, name = "Ingredient 2", unit = "g"),
      Ingredient(id = 3, name = "Ingredient 3", amount = 25.0),
    ),
    listOf(
      Ingredient(id = 1, name = "Ingredient 1", unit = "g"),
      Ingredient(id = 2, name = "Ingredient 2", unit = "g"),
      Ingredient(id = 3, name = "Ingredient 3"),
    ),
    listOf(
      Ingredient(id = 1, name = "Ingredient 1", amount = 100.0),
      Ingredient(id = 2, name = "Ingredient 2", amount = 2.0),
      Ingredient(id = 3, name = "Ingredient 3", amount = 19.12),
    ),
  )
}