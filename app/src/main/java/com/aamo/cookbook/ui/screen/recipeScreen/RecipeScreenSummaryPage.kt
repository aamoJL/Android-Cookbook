package com.aamo.cookbook.ui.screen.recipeScreen

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.service.IOService
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

  Column(modifier = Modifier.verticalScroll(scrollState)) {
    RecipeImage(uiState.recipeThumbnail, modifier = Modifier.height(250.dp))

    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
      CountInput(
        value = servingsState.current,
        label = when (servingsState.multiplier) {
          1f -> stringResource(R.string.input_title_servings)
          else -> "${stringResource(R.string.input_title_servings)} " +
                  "(${servingsState.multiplier.toStringWithoutZero(1)}x)"
        },
        onValueChange = onServingsCountChange,
        minValue = 1,
      )
      NoteCard(
        text = uiState.recipeNote,
        modifier = Modifier.fillMaxWidth()
      )
      Text(
        text = stringResource(R.string.page_title_ingredients),
        style = MaterialTheme.typography.headlineLarge,
        fontFamily = Handwritten
      )
      Column(verticalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier) {
        uiState.chaptersWithIngredients.forEach { chapterIngredientsPair ->
          IngredientCard(
            chapterName = chapterIngredientsPair.first,
            ingredients = chapterIngredientsPair.second,
            servingsMultiplier = servingsState.multiplier,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
private fun IngredientCard(
  chapterName: String,
  ingredients: List<Ingredient>,
  servingsMultiplier: Float,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = chapterName,
      fontFamily = Handwritten,
      style = MaterialTheme.typography.titleLarge,
    )
    IngredientList(
      ingredients = ingredients,
      servingsMultiplier = servingsMultiplier,
      modifier = Modifier.padding(horizontal = 8.dp),
    )
  }
}

@Composable
private fun RecipeImage(fileName: String, modifier: Modifier = Modifier) {
  if(fileName.isEmpty()) return
  Box(modifier = modifier) {
    Image(
      painter = rememberAsyncImagePainter(
        model = IOService(LocalContext.current)
          .getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
      ),
      contentDescription = null,
      contentScale = ContentScale.Crop,
      alignment = Alignment.Center,
      modifier = Modifier.fillMaxSize()
    )
  }
}