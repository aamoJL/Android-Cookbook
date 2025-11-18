package com.aamo.cookbook.ui.screen.recipeScreen

import android.content.Intent
import android.provider.AlarmClock
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.viewModel.RecipeScreenViewModel

data class CheckBoxTimerProperties(
  val title: String, val minutes: Int
)
// TODO: remove
@Composable
internal fun ChapterPage(
  uiState: RecipeScreenViewModel.ChapterPageUiState,
  servingsState: RecipeScreenViewModel.ServingsState,
  onProgressChange: (stepIndex: Int, value: Boolean) -> Unit
) {
  val scrollState = rememberScrollState()

  Surface {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
    ) {
      Text(
        text = "${uiState.chapter.chapter.orderNumber}. ${uiState.chapter.chapter.name}",
        fontFamily = Handwritten,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      )
      if (uiState.chapter.chapter.note.isNotEmpty()) {
        Box(modifier = Modifier.padding(8.dp)) {
          NoteCard(
            text = uiState.chapter.chapter.note, modifier = Modifier.fillMaxWidth()
          )
        }
      }
      Column {
        uiState.chapter.steps.forEachIndexed { index, step ->
          StepCheckBox(
            headline = "${step.step.description}${if (step.ingredients.isEmpty()) '.' else ':'}",
            ingredients = step.ingredients.filter { it.stepId == step.step.id },
            servingsMultiplier = servingsState.multiplier,
            checked = uiState.progress.elementAtOrElse(index) { false },
            onCheckedChange = { onProgressChange(index, it) },
            timerProperties = step.step.timerMinutes?.let { minutes ->
              CheckBoxTimerProperties(
                title = step.step.description, minutes = minutes
              )
            },
            note = step.step.note,
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
  servingsMultiplier: Float,
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  modifier: Modifier = Modifier,
  timerProperties: CheckBoxTimerProperties? = null,
  note: String = "",
  colors: ListItemColors = ListItemDefaults.colors()
) {
  val context = LocalContext.current
  ListItem(
    colors = colors, headlineContent = {
    Text(
      text = headline, fontFamily = Handwritten, fontWeight = FontWeight.Bold
    )
  }, supportingContent = if (ingredients.isNotEmpty() || note.isNotEmpty()) {
    {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (note.isNotEmpty()) {
          NoteCard(
            text = note, modifier = Modifier.fillMaxWidth()
          )
        }
        if (ingredients.isNotEmpty()) {
          Card(
            shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ), modifier = Modifier.fillMaxWidth()
          ) {
//            IngredientList(
//              ingredients = ingredients,
//              servingsMultiplier = servingsMultiplier,
//              modifier = Modifier.padding(8.dp),
//            )
          }
        }
      }
    }
  }
  else null, leadingContent = {
    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier) {
      Checkbox(checked = checked, onCheckedChange = null)
    }
  },
    // OverlineContent needs to be { } if the supporting content is not null,
    // otherwise the leadingContent will be aligned to center vertically.
    overlineContent = {}, trailingContent = if (timerProperties != null) {
      {
        IconButton(onClick = {
          val intent = Intent(AlarmClock.ACTION_SET_TIMER).putExtra(
            AlarmClock.EXTRA_LENGTH, timerProperties.minutes * 60
          ).putExtra(AlarmClock.EXTRA_MESSAGE, timerProperties.title)
            .putExtra(AlarmClock.EXTRA_SKIP_UI, false)
          context.startActivity(intent)
        }) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              painter = painterResource(id = R.drawable.baseline_alarm_24),
              contentDescription = stringResource(R.string.description_set_timer)
            )
            Text(
              text = stringResource(
                R.string.abbreviation_minutes, timerProperties.minutes
              )
            )
          }
        }
      }
    }
    else null, modifier = modifier.clickable { onCheckedChange(!checked) })
}