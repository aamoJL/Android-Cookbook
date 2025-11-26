package com.aamo.cookbook.features.recipe.view.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.HorizontalDivider
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
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.view.components.IngredientList
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.inputs.CountInput
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.extensions.general.toStringWithoutZero

@Composable
fun RecipeSummaryScreen(
  recipe: RecipeWithChaptersStepsAndIngredients,
  servings: Int,
  servingsMultiplier: Double,
  onServingsChange: (Int) -> Unit,
) {
  val scrollState = rememberScrollState()

  Surface {
    Column(modifier = Modifier.verticalScroll(scrollState)) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.height(250.dp)
      ) {
        Image(
          painter = rememberAsyncImagePainter(
            model = PhotoService(LocalContext.current).get(recipe.recipe.thumbnailUri)
          ),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          alignment = Alignment.Center,
          modifier = Modifier.fillMaxSize()
        )
      }
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth()
      ) {
        Icon(
          painter = painterResource(R.drawable.baseline_local_dining_24),
          contentDescription = stringResource(R.string.cd_servings),
        )
        CountInput(
          value = servings, onValueChange = onServingsChange, minValue = 1
        )
        if (servingsMultiplier != 1.0) {
          @Suppress("HardCodedStringLiteral") Text(
            text = "( ${servingsMultiplier.toStringWithoutZero(decimalCount = 1)}x )",
            style = MaterialTheme.typography.labelLarge
          )
        }
      }
      HorizontalDivider()
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
      ) {
        if (recipe.recipe.note.isNotEmpty()) {
          NoteCard(text = recipe.recipe.note, modifier = Modifier.fillMaxWidth())
        }
        Text(
          text = stringResource(R.string.title_ingredients),
          style = MaterialTheme.typography.headlineMedium,
          fontFamily = Handwritten,
          modifier = Modifier.fillMaxWidth()
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          recipe.chapters.map { it.chapter to it.steps.flatMap { (_, ingredients) -> ingredients } }
            .forEach { chapterIngredientsPair ->
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 0.dp)
              ) {
                Text(
                  text = chapterIngredientsPair.first.name,
                  fontFamily = Handwritten,
                  style = MaterialTheme.typography.labelSmall,
                )
                IngredientList(
                  ingredients = chapterIngredientsPair.second,
                  servingsMultiplier = servingsMultiplier,
                  fontFamily = Handwritten,
                  textStyle = MaterialTheme.typography.bodyMedium
                )
              }
            }
        }
      }
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeSummaryScreen(
      recipe = RecipeWithChaptersStepsAndIngredients(
        recipe = Recipe(name = "Recipe 1", note = "Note!"), chapters = listOf(
          ChapterWithStepsAndIngredients(
            chapter = Chapter(name = "Chapter 1"), steps = listOf(
              StepWithIngredients(
                step = Step(), ingredients = listOf(
                  Ingredient(name = "Ingredient 1", amount = 250.0, unit = "g"),
                )
              )
            )
          ),
          ChapterWithStepsAndIngredients(
            chapter = Chapter(name = "Chapter 2"), steps = listOf(
              StepWithIngredients(
                step = Step(), ingredients = listOf(
                  Ingredient(name = "Ingredient 3", amount = 100.0)
                )
              )
            )
          )
        )
      ), servings = 2, servingsMultiplier = 1.5, onServingsChange = {})
  }
}