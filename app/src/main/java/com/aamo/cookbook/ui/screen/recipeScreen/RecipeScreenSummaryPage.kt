package com.aamo.cookbook.ui.screen.recipeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.CountInput
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.RecipeScreenViewModel

@Composable
internal fun SummaryPage(
  uiState: RecipeScreenViewModel.SummaryPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  onServingsCountChange: (count: Int) -> Unit,
) {
  Column(modifier = Modifier
    .fillMaxSize()
    .padding(8.dp)
  ) {
    Row(verticalAlignment = Alignment.Bottom) {
      Text(
        text = stringResource(R.string.page_title_ingredients),
        fontFamily = Handwritten,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
          .weight(1f)
          .padding(vertical = 4.dp)
      )
      CountInput(
        value = servingsState.current,
        label = when (servingsState.multiplier) {
          1f -> stringResource(R.string.input_title_servings)
          else -> "${stringResource(R.string.input_title_servings)} (${servingsState.multiplier.toStringWithoutZero(1)}x)"
        },
        onValueChange = {
          onServingsCountChange(it)
        },
        minValue = 1,
      )
    }

    Column(modifier = Modifier) {
      uiState.chaptersWithIngredients.forEach { chapterIngredientPair ->
        Box(modifier = Modifier.padding(vertical = 4.dp)) {
          Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
              text = chapterIngredientPair.first,
              style = MaterialTheme.typography.titleLarge,
              fontFamily = Handwritten,
              modifier = Modifier.padding(vertical = 4.dp)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
              IngredientList(
                ingredients = chapterIngredientPair.second,
                servingsMultiplier = servingsState.multiplier
              )
            }
          }
        }
      }
    }
  }
}