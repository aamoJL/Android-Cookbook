package com.aamo.cookbook.features.recipe.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.view.components.IngredientList
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.ui.theme.Handwritten
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RecipeChapterScreen(
  chapter: ChapterWithStepsAndIngredients,
  servingsMultiplier: Double,
  progress: List<Boolean>,
  onProgressChange: (List<Boolean>) -> Unit,
  onStartTimer: (title: String, duration: Duration) -> Unit
) {
  val scrollState = rememberScrollState()

  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
    ) {
      Text(
        text = "${chapter.chapter.orderNumber}. ${chapter.chapter.name}",
        fontFamily = Handwritten,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      )
      if (chapter.chapter.note.isNotEmpty()) {
        Box(modifier = Modifier.padding(8.dp)) {
          NoteCard(text = chapter.chapter.note, modifier = Modifier.fillMaxWidth())
        }
      }
      Column {
        chapter.steps.forEachIndexed { index, step ->
          StepCheckBox(
            headline = "${step.step.description}${if (step.ingredients.isEmpty()) '.' else ':'}",
            ingredients = step.ingredients.filter { it.stepId == step.step.id },
            servingsMultiplier = servingsMultiplier,
            checked = progress.elementAtOrElse(index) { false },
            note = step.step.note,
            timerDuration = step.step.timerMinutes?.minutes,
            onCheckedChange = {
              onProgressChange(
                progress.toMutableList().apply { this[index] = it })
            },
            onStartTimer = { duration ->
              onStartTimer(step.step.description, duration)
            },
          )
        }
      }
    }
  }
}

@Composable
private fun StepCheckBox(
  headline: String,
  ingredients: List<Ingredient>,
  servingsMultiplier: Double,
  checked: Boolean,
  note: String,
  timerDuration: Duration?,
  onCheckedChange: (checked: Boolean) -> Unit,
  onStartTimer: (Duration) -> Unit,
  modifier: Modifier = Modifier,
  colors: ListItemColors = ListItemDefaults.colors()
) {
  ListItem(
    colors = colors, headlineContent = {
    Text(text = headline, fontFamily = Handwritten, fontWeight = FontWeight.Bold)
  }, supportingContent = if (ingredients.isNotEmpty() || note.isNotEmpty()) {
    {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (note.isNotEmpty()) {
          NoteCard(text = note, modifier = Modifier.fillMaxWidth())
        }
        if (ingredients.isNotEmpty()) {
          Card(
            shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ), modifier = Modifier.fillMaxWidth()
          ) {
            IngredientList(
              ingredients = ingredients,
              servingsMultiplier = servingsMultiplier,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }
  }
  else null, leadingContent = {
    Box(contentAlignment = Alignment.TopCenter) {
      Checkbox(checked = checked, onCheckedChange = null)
    }
  },
    // OverlineContent needs to be { } if the supporting content is not null,
    // otherwise the leadingContent will be aligned to center vertically.
    overlineContent = {}, trailingContent = if (timerDuration != null) {
      {
        IconButton(onClick = { onStartTimer(timerDuration) }) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              painter = painterResource(id = R.drawable.baseline_alarm_24),
              contentDescription = stringResource(R.string.description_set_timer)
            )
            Text(text = stringResource(R.string.abbreviation_minutes, timerDuration.inWholeMinutes))
          }
        }
      }
    }
    else null, modifier = modifier.clickable { onCheckedChange(!checked) })
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun Preview() {
  RecipeChapterScreen(
    chapter = ChapterWithStepsAndIngredients(
    chapter = Chapter(orderNumber = 1, name = "Chapter 1", note = "Note"), steps = listOf(
      StepWithIngredients(
        step = Step(
          orderNumber = 1, description = "Lorem ipsum", timerMinutes = 10, note = "Note"
        ), ingredients = listOf(
          Ingredient(name = "Ing", amount = 2.0, unit = "g"),
          Ingredient(name = "Ing", amount = 2.0, unit = "g"),
          Ingredient(name = "Ing", amount = 2.0, unit = "g"),
        )
      ),
      StepWithIngredients(
        step = Step(orderNumber = 2, description = "Lorem ipsum"), ingredients = listOf(
          Ingredient(name = "Ing", amount = 2.0, unit = "g"),
          Ingredient(name = "Ing", amount = 2.0, unit = "g"),
        )
      ),
      StepWithIngredients(
        step = Step(orderNumber = 3, description = "Lorem ipsum"), ingredients = listOf()
      ),
    )
  ),
    servingsMultiplier = 1.0,
    progress = listOf(true, false, false),
    onProgressChange = {},
    onStartTimer = { _, _ -> })
}