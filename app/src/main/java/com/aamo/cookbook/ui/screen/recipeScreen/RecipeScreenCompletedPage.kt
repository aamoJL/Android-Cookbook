package com.aamo.cookbook.ui.screen.recipeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.FiveStarRating
import com.aamo.cookbook.viewModel.RecipeScreenViewModel

@Composable
internal fun CompletedPage(
  uiState: RecipeScreenViewModel.CompletedPageUiState,
  onRatingChange: (Int) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) {
      Card {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(8.dp)
        ) {
          Text(text = "Rate the recipe")
          FiveStarRating(
            value = uiState.fiveStarRating,
            onValueChange = onRatingChange
          )
        }
      }
    }
  }
}