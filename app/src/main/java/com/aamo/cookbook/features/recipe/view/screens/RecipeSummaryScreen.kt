package com.aamo.cookbook.features.recipe.view.screens

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.aamo.cookbook.features.recipe.view.components.IngredientCheckBoxList
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.inputs.CountInput
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.toStringWithoutZero
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList

@Composable
fun RecipeSummaryScreen(
  recipe: RecipeWithChaptersStepsAndIngredients,
  ingredientSelection: ViewModelStateList<Long>,
  servings: Int,
  servingsMultiplier: Double,
  onServingsChange: (Int) -> Unit,
) {
  val scrollState = rememberScrollState()
  val totalTime by remember(recipe) {
    mutableIntStateOf(recipe.chapters.sumOf { chapter ->
      chapter.steps.sumOf { step ->
        step.step.timerMinutes ?: 0
      }
    })
  }

  BackgroundSurface(modifier = Modifier.fillMaxSize()) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .verticalScroll(scrollState)
        .padding(8.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(intrinsicSize = IntrinsicSize.Max)
      ) {
        Column(modifier = Modifier.weight(1f)) {
          ElevatedCard(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
          ) {
            Thumbnail(fileName = recipe.recipe.thumbnailUri, modifier = Modifier.aspectRatio(1f))
          }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          ElevatedCard(
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxSize()
          ) {
            SideInfo(
              servings = servings,
              onServingsChange = onServingsChange,
              servingsMultiplier = servingsMultiplier,
              totalTime = totalTime,
              modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp),
            )
          }
        }
      }
      if (recipe.recipe.note.isNotEmpty()) {
        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
          NoteCard(
            text = recipe.recipe.note, modifier = Modifier.fillMaxWidth()
          )
        }
      }
      HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 32.dp))
      ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .width(intrinsicSize = IntrinsicSize.Max)
          .align(Alignment.CenterHorizontally)
      ) {
        IngredientList(
          recipe = recipe,
          ingredientSelection = ingredientSelection,
          servingsMultiplier = servingsMultiplier,
          modifier = Modifier.padding(vertical = 24.dp, horizontal = 32.dp)
        )
      }
    }
  }
}

@Composable
fun SideInfo(
  modifier: Modifier = Modifier,
  servings: Int,
  onServingsChange: (Int) -> Unit,
  servingsMultiplier: Double,
  totalTime: Int,
) {
  Column(
    verticalArrangement = Arrangement.SpaceEvenly, modifier = modifier
  ) {
    Row {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          painter = painterResource(R.drawable.baseline_alarm_24),
          contentDescription = stringResource(R.string.cd_time),
          modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
          text = stringResource(R.string.abbreviation_minutes, totalTime),
          style = MaterialTheme.typography.labelSmall
        )
      }
    }
    Row {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(
          painter = painterResource(R.drawable.baseline_local_dining_24),
          contentDescription = stringResource(R.string.cd_servings),
          modifier = Modifier.padding(bottom = 2.dp)
        )
        CountInput(
          value = servings,
          onValueChange = onServingsChange,
          minValue = 1,
        )
        Text(
          text = if (servingsMultiplier != 1.0) "${
            servingsMultiplier.toStringWithoutZero(decimalCount = 1)
          }x"
          else String.EMPTY,
          style = MaterialTheme.typography.labelSmall,
        )
      }
    }
  }
}

@Composable
private fun Thumbnail(fileName: String, modifier: Modifier = Modifier) {
  Surface(modifier = modifier) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
      if (fileName.isNotEmpty()) {
        Image(
          painter = rememberAsyncImagePainter(
            model = IOService(LocalContext.current).getExternalFileUri(
              Environment.DIRECTORY_PICTURES, fileName
            )
          ),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }
      else {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            painter = painterResource(R.drawable.baseline_no_photography_24),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f),
            contentDescription = null,
          )
        }
      }
    }
  }
}

@Composable
fun IngredientList(
  recipe: RecipeWithChaptersStepsAndIngredients,
  ingredientSelection: ViewModelStateList<Long>,
  servingsMultiplier: Double,
  modifier: Modifier = Modifier,
) {
  Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
    HorizontalDividerLabel(
      label = stringResource(R.string.title_ingredients),
      style = MaterialTheme.typography.titleLarge,
      fontFamily = Handwritten,
      color = MaterialTheme.colorScheme.inversePrimary,
      minLineWidth = 30.dp
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
      recipe.chapters.map { it.chapter to it.steps.flatMap { (_, ingredients) -> ingredients } }
        .forEach { chapterIngredientsPair ->
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = chapterIngredientsPair.first.name,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f),
              modifier = Modifier.fillMaxWidth()
            )
            IngredientCheckBoxList(
              ingredients = chapterIngredientsPair.second,
              ingredientSelection = ingredientSelection,
              servingsMultiplier = servingsMultiplier,
              softWrap = false,
              textStyle = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
    }
  }
}

@Suppress("HardCodedStringLiteral", "SpellCheckingInspection")
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
                  Ingredient(id = 1, name = "Ingnt 1", amount = 250.0, unit = "g"),
                  Ingredient(name = "Ingrediet 2", amount = 250.0, unit = "g"),
                  Ingredient(name = "Ingredient 3", amount = 250.0),
                )
              )
            )
          ), ChapterWithStepsAndIngredients(
            chapter = Chapter(name = "Chapter 2"), steps = listOf(
              StepWithIngredients(
                step = Step(), ingredients = listOf(
                  Ingredient(name = "Ingredient 3")
                )
              )
            )
          )
        )
      ),
      servings = 2,
      servingsMultiplier = 1.5,
      onServingsChange = {},
      ingredientSelection = ViewModelStateList(items = listOf(1)),
    )
  }
}