package com.aamo.cookbook.ui.screen.recipeScreen

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.components.CountInput
import com.aamo.cookbook.ui.components.NoteCard
import com.aamo.cookbook.ui.theme.CookbookTheme
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

  Surface {
    Column(modifier = Modifier.verticalScroll(scrollState)) {
      RecipeImage(uiState.recipeThumbnail, modifier = Modifier.height(250.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth()
      ) {
        Icon(
          painter = painterResource(R.drawable.baseline_local_dining_24),
          contentDescription = stringResource(R.string.description_servings),
        )
        CountInput(
          value = servingsState.current,
          onValueChange = onServingsCountChange,
          minValue = 1
        )
        if (servingsState.multiplier != 1f) {
          Text(
            text = "( ${servingsState.multiplier.toStringWithoutZero(decimalCount = 1)}x )",
            style = MaterialTheme.typography.labelLarge
          )
        }
      }
      Divider()
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
      ) {
        NoteCard(
          text = uiState.recipeNote,
          modifier = Modifier.fillMaxWidth()
        )
        Text(
          text = stringResource(R.string.page_title_ingredients),
          style = MaterialTheme.typography.headlineMedium,
          fontFamily = Handwritten,
          modifier = Modifier.fillMaxWidth()
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier) {
          uiState.chaptersWithIngredients.forEach { chapterIngredientsPair ->
            IngredientCard(
              chapterName = chapterIngredientsPair.first,
              ingredients = chapterIngredientsPair.second,
              servingsMultiplier = servingsState.multiplier,
              modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
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
      style = MaterialTheme.typography.labelSmall,
    )
    IngredientList(
      ingredients = ingredients,
      servingsMultiplier = servingsMultiplier,
      fontFamily = Handwritten,
      textStyle = MaterialTheme.typography.bodyMedium
    )
  }
}

@Composable
private fun RecipeImage(fileName: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
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

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    SummaryPage(
      uiState = RecipeScreenViewModel.SummaryPageUiState(
        recipeName = "Recipe 1",
        recipeNote = "Recipe Note",
        chaptersWithIngredients = listOf(
          Pair(
            "Chapter 1", listOf(
              Ingredient(name = "Ingredient 1", amount = 250f, unit = "g"),
              Ingredient(name = "Ingredient 2", amount = 25f, unit = "dl")
            )
          ),
          Pair(
            "Chapter 2", listOf(
              Ingredient(name = "Ingredient 1", amount = 250f, unit = "g"),
              Ingredient(name = "Ingredient 2", amount = 25f, unit = "dl")
            )
          )
        )
      ),
      servingsState = RecipeScreenViewModel.ServingsState(),
      onServingsCountChange = {})
  }
}