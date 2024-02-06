package com.aamo.cookbook.ui.screen.recipeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.CountInput
import com.aamo.cookbook.ui.components.NoteCard
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.RecipeScreenViewModel

@Composable
internal fun SummaryPage(
  uiState: RecipeScreenViewModel.SummaryPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  onServingsCountChange: (count: Int) -> Unit,
) {
  val scrollState = rememberScrollState()

  Column(modifier = Modifier
    .fillMaxSize()
    .padding(8.dp)
    .verticalScroll(scrollState)
  ) {
    if(uiState.recipeNote.isNotEmpty()) {
      NoteCard(
        text = uiState.recipeNote,
        modifier = Modifier.fillMaxWidth()
      )
    }

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
          else -> "${stringResource(R.string.input_title_servings)} (${
            servingsState.multiplier.toStringWithoutZero(
              1
            )
          }x)"
        },
        onValueChange = onServingsCountChange,
        minValue = 1,
      )
    }

    Column(modifier = Modifier) {
      uiState.chaptersWithIngredients.forEach { chapterIngredientPair ->
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          Text(
            text = chapterIngredientPair.first,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = Handwritten,
            modifier = Modifier.padding(vertical = 4.dp)
          )
          IngredientList(
            ingredients = chapterIngredientPair.second,
            servingsMultiplier = servingsState.multiplier,
            modifier = Modifier.padding(horizontal = 16.dp)
          )
        }
      }
    }
  }
}