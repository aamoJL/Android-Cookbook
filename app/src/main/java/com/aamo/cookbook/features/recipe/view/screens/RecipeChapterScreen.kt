package com.aamo.cookbook.features.recipe.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.view.components.IngredientList
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.extensions.general.grayScale
import com.aamo.cookbook.utility.tags.UITag
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RecipeChapterScreen(
  chapter: ChapterWithStepsAndIngredients,
  servingsMultiplier: Double,
  progress: List<Boolean>,
  onProgressChange: (List<Boolean>) -> Unit,
  onStartTimer: (title: String, duration: Duration) -> Unit,
  isCurrentChapter: Boolean = false,
) {
  val scrollState = rememberScrollState()

  BackgroundSurface(modifier = Modifier.fillMaxSize()) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxSize()
        .padding(12.dp)
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.widthIn(max = 400.dp)
      ) {
        HorizontalDividerLabel(
          label = "${chapter.chapter.orderNumber}. ${chapter.chapter.name}",
          fontFamily = Handwritten,
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(horizontal = 28.dp)
        )
        if (chapter.chapter.note.isNotEmpty()) {
          NoteCard(text = chapter.chapter.note, modifier = Modifier.fillMaxWidth())
          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          chapter.steps.forEachIndexed { index, step ->
            val checked = progress.elementAtOrElse(index) { false }

            StepCheckBox(
              headline = "${step.step.description}${if (step.ingredients.isEmpty()) '.' else ':'}",
              ingredients = step.ingredients.filter { it.stepId == step.step.id },
              servingsMultiplier = servingsMultiplier,
              checked = checked,
              note = step.step.note,
              timerDuration = step.step.timerMinutes?.minutes,
              onCheckedChange = {
                onProgressChange(
                  progress.toMutableList().apply { this[index] = it })
              },
              current = isCurrentChapter && !checked && progress.take(index).let {
                it.isEmpty() || it.all { value -> value }
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
}

@Composable
private fun StepCheckBox(
  headline: String,
  ingredients: List<Ingredient>,
  servingsMultiplier: Double,
  checked: Boolean,
  current: Boolean,
  note: String,
  timerDuration: Duration?,
  onCheckedChange: (checked: Boolean) -> Unit,
  onStartTimer: (Duration) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(4.dp),
    shadowElevation = 1.dp,
    border = if (current) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
    modifier = modifier
      .toggleable(
        value = checked, onValueChange = onCheckedChange, role = Role.Checkbox
      )
      .testTag(UITag.CHECK.name),
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(12.dp)
        .fillMaxSize()
    ) {
      Column {
        Checkbox(checked = checked, onCheckedChange = null)
      }
      Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .weight(1f)
          .grayScale(enabled = checked)
      ) {
        Row {
          Text(
            text = headline,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (checked) .5f else 1f),
            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
          )
        }
        if (note.isNotEmpty()) {
          Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
            NoteCard(
              text = note, colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = if (checked) .5f else 1f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
              ), modifier = Modifier.fillMaxWidth()
            )
          }
        }
        Row {
          if (ingredients.isNotEmpty()) {
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (checked) .5f else 1f),
              shape = RoundedCornerShape(4.dp),
            ) {
              IngredientList(
                ingredients = ingredients,
                servingsMultiplier = servingsMultiplier,
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                  .padding(vertical = 8.dp, horizontal = 16.dp)
                  .fillMaxWidth()
              )
            }
          }
        }
      }
      if (timerDuration != null) {
        Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          IconButton(onClick = { onStartTimer(timerDuration) }, enabled = !checked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                painter = painterResource(id = R.drawable.baseline_alarm_24),
                contentDescription = stringResource(R.string.cd_set_timer)
              )
              Text(
                text = stringResource(
                  R.string.abbreviation_minutes, timerDuration.inWholeMinutes
                ), style = MaterialTheme.typography.labelSmall
              )
            }
          }
        }
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeChapterScreen(
      chapter = ChapterWithStepsAndIngredients(
        chapter = Chapter(orderNumber = 1, name = "Chapter 1", note = "Note"), steps = listOf(
          StepWithIngredients(
            step = Step(
              orderNumber = 1, description = "Lorem ipsum", timerMinutes = 120, note = "Note"
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
      isCurrentChapter = true,
      progress = listOf(true, false, false),
      onProgressChange = {},
      onStartTimer = { _, _ -> })
  }
}