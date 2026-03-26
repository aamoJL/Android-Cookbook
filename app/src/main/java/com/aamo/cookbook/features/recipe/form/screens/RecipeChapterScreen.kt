package com.aamo.cookbook.features.recipe.form.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.features.recipe.view.components.NoteCard
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.inputs.number_field.NullableDoubleFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.ui.components.inputs.text_field.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.modals.DeleteDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.Zero
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel
import com.aamo.cookbook.utility.extensions.general.ifElse
import com.aamo.cookbook.utility.extensions.general.toFractionFormattedString
import java.util.UUID

@Composable
fun RecipeChapterScreen(
  index: Int,
  formState: RecipeFormViewModel.FormChapterState,
  onDelete: () -> Unit,
  onMoveLeft: (() -> Unit)?,
  onMoveRight: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_chapter),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onDelete()
    },
  )

  LazyColumn(
    horizontalAlignment = Alignment.CenterHorizontally,
    contentPadding = PaddingValues(bottom = 140.dp),
    modifier = modifier.fillMaxSize()
  ) {
    item {
      Column {
        HorizontalDividerLabel(
          label = stringResource(R.string.title_chapter_information, index + 1),
          modifier = Modifier.padding(12.dp)
        )
        ElevatedCard(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
          ),
        ) {
          Column {
            Column(
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier
                .animateContentSize()
                .padding(8.dp)
                .padding(bottom = 4.dp)
                .fillMaxWidth()
            ) {
              TextField(
                value = formState.name.value,
                onValueChange = { formState.name.update(it) },
                label = { Text(stringResource(R.string.label_name)) },
                shape = RectangleShape,
                colors = borderlessTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                  capitalization = KeyboardCapitalization.Sentences,
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
              )
              if (formState.noteFieldToggleValue) {
                TextField(
                  value = formState.note.value,
                  onValueChange = { formState.note.update(it) },
                  label = { Text(stringResource(R.string.label_note).asOptionalLabel()) },
                  shape = RectangleShape,
                  colors = borderlessTextFieldColors(),
                  keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                  ),
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
            Surface {
              Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
              ) {
                IconButton(enabled = onMoveLeft != null, onClick = { onMoveLeft?.invoke() }) {
                  Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_left_24px),
                    contentDescription = stringResource(R.string.cd_move_left),
                  )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  IconButton(onClick = { openDeleteDialog = true }) {
                    Icon(
                      painter = painterResource(R.drawable.rounded_delete_24),
                      contentDescription = stringResource(R.string.cd_delete_chapter),
                      tint = MaterialTheme.colorScheme.error
                    )
                  }
                  IconToggleButton(
                    enabled = formState.note.value.isEmpty(),
                    checked = formState.noteFieldToggleValue,
                    onCheckedChange = { formState.noteFieldToggleValue = it },
                    colors = IconButtonDefaults.iconToggleButtonColors(
                      checkedContentColor = MaterialTheme.colorScheme.tertiary,
                      disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f)
                    )
                  ) {
                    Icon(
                      painter = painterResource(R.drawable.sticky_note_2_24px),
                      contentDescription = stringResource(R.string.cd_toggle_note_field),
                    )
                  }
                }
                IconButton(enabled = onMoveRight != null, onClick = { onMoveRight?.invoke() }) {
                  Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_right_24px),
                    contentDescription = stringResource(R.string.cd_move_right),
                  )
                }
              }
            }
          }
        }
        HorizontalDividerLabel(
          label = stringResource(R.string.title_steps), modifier = Modifier.padding(12.dp)
        )
      }
    }
    itemsIndexed(items = formState.steps.values, key = { _, x -> x.id }) { i, step ->
      if (formState.selectedStepId == step.id) {
        StepForm(
          formState = step,
          onMoveUp = ifElse(
            condition = i == 0,
            ifTrue = { null },
            ifFalse = { { formState.steps.swapAt(i, i - 1) } }),
          onMoveDown = ifElse(
            condition = i == formState.steps.values.size - 1,
            ifTrue = { null },
            ifFalse = { { formState.steps.swapAt(i, i + 1) } }),
          onDelete = { formState.steps.removeAt(i) },
          onUnselect = { formState.selectedStepId = null },
          modifier = Modifier
            .animateItem()
            .padding(bottom = 8.dp)
        )
      }
      else {
        StepDisplay(
          formState = step,
          onMoveUp = ifElse(
            condition = i == 0,
            ifTrue = { null },
            ifFalse = { { formState.steps.swapAt(i, i - 1) } }),
          onMoveDown = ifElse(
            condition = i == formState.steps.values.size - 1,
            ifTrue = { null },
            ifFalse = { { formState.steps.swapAt(i, i + 1) } }),
          modifier = Modifier
            .clickable { formState.selectedStepId = step.id }
            .animateItem()
            .padding(bottom = 8.dp))
      }
    }
    item {
      Box(modifier = Modifier.padding(top = 8.dp)) {
        FilledIconButton(
          onClick = {
            formState.addStep().also {
              formState.selectedStepId = it.id // Select the added step
            }
          },
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier
            .size(48.dp)
            .shadow(elevation = 2.dp, shape = CircleShape),
        ) {
          Icon(
            painter = painterResource(R.drawable.rounded_add_24),
            contentDescription = stringResource(R.string.cd_add_step)
          )
        }
      }
    }
  }
}

@Composable
private fun StepDisplay(
  formState: RecipeFormViewModel.FormStepState,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    ElevatedCard(
      shape = MaterialTheme.shapes.small,
      colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
      ),
    ) {
      Column(
        modifier = Modifier
          .animateContentSize()
          .height(intrinsicSize = IntrinsicSize.Max)
      ) {
        Row(modifier = Modifier.weight(1f)) {
          Surface {
            Column(
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.fillMaxHeight(),
            ) {
              if (formState.timerMinutes.value.let { it != null && it > 0 }) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.minimumInteractiveComponentSize(),
                ) {
                  Icon(
                    painter = painterResource(R.drawable.baseline_alarm_24),
                    contentDescription = stringResource(R.string.label_step_timer)
                  )
                  Text(
                    text = stringResource(
                      R.string.abbreviation_minutes, formState.timerMinutes.value.toString()
                    ), style = MaterialTheme.typography.labelSmall
                  )
                }
              }
              else {
                Spacer(modifier = Modifier.minimumInteractiveComponentSize())
              }
            }
          }
          VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .weight(1f)
              .padding(8.dp)
              .fillMaxWidth()
          ) {
            if (formState.description.value.isEmpty()) {
              Text(
                text = stringResource(R.string.text_click_to_edit_step),
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.titleMedium,
              )
            }
            else {
              Text(text = formState.description.value, style = MaterialTheme.typography.titleMedium)
            }
            if (formState.note.value.isNotEmpty()) {
              NoteCard(
                text = formState.note.value,
                label = String.EMPTY,
                modifier = Modifier.fillMaxWidth()
              )
            }
            if (formState.ingredients.values.isNotEmpty()) {
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraSmall,
              ) {
                IngredientList(
                  ingredients = formState.ingredients.values,
                  textStyle = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .fillMaxWidth()
                )
              }
            }
          }
          VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
          Surface {
            Column(
              verticalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxHeight(),
            ) {
              IconButton(
                enabled = onMoveUp != null,
                onClick = { onMoveUp?.invoke() },
              ) {
                Icon(
                  painter = painterResource(R.drawable.rounded_keyboard_arrow_up_24),
                  contentDescription = stringResource(R.string.cd_move_up)
                )
              }
              IconButton(
                enabled = onMoveDown != null,
                onClick = { onMoveDown?.invoke() },
              ) {
                Icon(
                  painter = painterResource(R.drawable.rounded_keyboard_arrow_down_24),
                  contentDescription = stringResource(R.string.cd_move_down)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun IngredientList(
  ingredients: List<RecipeFormViewModel.FormIngredientState>,
  modifier: Modifier = Modifier,
  fontFamily: FontFamily = FontFamily.Default,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
    if (ingredients.any { it.amount.value != Double.Zero }) {
      Column(modifier = Modifier.width(IntrinsicSize.Max)) {
        ingredients.forEach {
          Text(
            text = if (it.amount.value == Double.Zero) String.EMPTY
            else it.amount.value?.toFractionFormattedString() ?: String.EMPTY,
            style = textStyle,
            fontFamily = fontFamily,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
    if (ingredients.any { it.unit.value.isNotEmpty() }) {
      Column(modifier = Modifier.defaultMinSize(minWidth = 20.dp)) {
        ingredients.forEach {
          Text(
            text = it.unit.value, style = textStyle, fontFamily = fontFamily
          )
        }
      }
    }
    Column {
      ingredients.forEach { Text(text = it.name.value, style = textStyle, fontFamily = fontFamily) }
    }
  }
}

@Composable
private fun StepForm(
  formState: RecipeFormViewModel.FormStepState,
  onMoveUp: (() -> Unit)?,
  onMoveDown: (() -> Unit)?,
  onDelete: () -> Unit,
  onUnselect: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_step),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onDelete()
    },
  )

  Box(modifier = modifier) {
    ElevatedCard(
      shape = MaterialTheme.shapes.small,
      colors = CardDefaults.elevatedCardColors(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
      ),
    ) {
      Column(
        modifier = Modifier
          .animateContentSize()
          .height(intrinsicSize = IntrinsicSize.Max)
      ) {
        Row(
          modifier = Modifier
            .animateContentSize()
            .weight(1f)
        ) {
          Surface {
            Column(
              verticalArrangement = Arrangement.Top,
              modifier = Modifier.fillMaxHeight(),
            ) {
              IconToggleButton(
                enabled = formState.timerMinutes.value?.let { it > 0 } != true,
                checked = formState.timerFieldToggleValue,
                onCheckedChange = { formState.timerFieldToggleValue = it },
                colors = IconButtonDefaults.iconToggleButtonColors(
                  checkedContentColor = MaterialTheme.colorScheme.tertiary,
                  disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f)
                )
              ) {
                Icon(
                  painter = painterResource(R.drawable.baseline_alarm_24),
                  contentDescription = stringResource(R.string.cd_toggle_timer_field)
                )
              }
              IconToggleButton(
                enabled = formState.note.value.isEmpty(),
                checked = formState.noteFieldToggleValue,
                onCheckedChange = { formState.noteFieldToggleValue = it },
                colors = IconButtonDefaults.iconToggleButtonColors(
                  checkedContentColor = MaterialTheme.colorScheme.tertiary,
                  disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f)
                )
              ) {
                Icon(
                  painter = painterResource(R.drawable.sticky_note_2_24px),
                  contentDescription = stringResource(R.string.cd_toggle_note_field)
                )
              }
              IconButton(onClick = { openDeleteDialog = true }) {
                Icon(
                  painter = painterResource(R.drawable.rounded_delete_24),
                  contentDescription = stringResource(R.string.cd_delete_chapter),
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
          VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
          Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .weight(1f)
              .padding(8.dp)
              .padding(bottom = 4.dp)
              .fillMaxWidth()
          ) {
            TextField(
              value = formState.description.value,
              onValueChange = { formState.description.update(it) },
              label = { Text(stringResource(R.string.label_description)) },
              shape = RectangleShape,
              colors = borderlessTextFieldColors(),
              keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
              ),
              modifier = Modifier.fillMaxWidth()
            )
            if (formState.timerFieldToggleValue) {
              NumberField(
                value = formState.timerMinutes.value,
                onValueChange = { formState.timerMinutes.update(it) },
                validator = NullableIntFieldValidator,
                label = { Text(stringResource(R.string.label_step_timer).asOptionalLabel()) },
                shape = RectangleShape,
                suffix = { Text(text = stringResource(R.string.suffix_minutes)) },
                colors = borderlessTextFieldColors(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
              )
            }
            if (formState.noteFieldToggleValue) {
              TextField(
                value = formState.note.value,
                onValueChange = { formState.note.update(it) },
                label = { Text(stringResource(R.string.label_note).asOptionalLabel()) },
                shape = RectangleShape,
                colors = borderlessTextFieldColors(),
                keyboardOptions = KeyboardOptions(
                  capitalization = KeyboardCapitalization.Sentences,
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
              )
            }
          }
          VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
          Surface {
            Column(
              verticalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxHeight(),
            ) {
              IconButton(
                enabled = onMoveUp != null,
                onClick = { onMoveUp?.invoke() },
              ) {
                Icon(
                  painter = painterResource(R.drawable.rounded_keyboard_arrow_up_24),
                  contentDescription = stringResource(R.string.cd_move_up)
                )
              }
              IconButton(onClick = onUnselect) {
                Icon(
                  painter = painterResource(R.drawable.rounded_check_24),
                  contentDescription = stringResource(R.string.cd_unselect)
                )
              }
              IconButton(
                enabled = onMoveDown != null,
                onClick = { onMoveDown?.invoke() },
              ) {
                Icon(
                  painter = painterResource(R.drawable.rounded_keyboard_arrow_down_24),
                  contentDescription = stringResource(R.string.cd_move_down)
                )
              }
            }
          }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
        Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)) {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
          ) {
            if (formState.ingredients.values.isNotEmpty()) {
              HorizontalDividerLabel(
                label = stringResource(R.string.title_ingredients),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                formState.ingredients.values.forEachIndexed { i, ingredient ->
                  IngredientForm(
                    formState = ingredient,
                    onDelete = { formState.ingredients.removeAt(i) },
                  )
                }
              }
            }
            Button(
              onClick = { formState.addIngredient() },
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  painter = painterResource(R.drawable.rounded_add_24),
                  contentDescription = stringResource(R.string.cd_add_ingredient)
                )
                Text(stringResource(R.string.btn_ingredient))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun IngredientForm(
  formState: RecipeFormViewModel.FormIngredientState,
  onDelete: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    TextField(
      value = formState.name.value,
      onValueChange = { formState.name.update(it) },
      label = {
        Text(
          text = stringResource(R.string.label_name),
          softWrap = false,
          fontSize = MaterialTheme.typography.bodyLarge.fontSize / 1.5
        )
      },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Sentences,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
      ),
      textStyle = MaterialTheme.typography.bodyMedium,
      singleLine = true,
      modifier = Modifier.weight(2f)
    )
    NumberField(
      value = formState.amount.value,
      onValueChange = { formState.amount.update(it) },
      validator = NullableDoubleFieldValidator,
      label = {
        Text(
          text = stringResource(R.string.label_amount).asOptionalLabel(),
          softWrap = false,
          fontSize = MaterialTheme.typography.bodyLarge.fontSize / 1.5
        )
      },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next
      ),
      textStyle = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.weight(1f)
    )
    TextField(
      value = formState.unit.value,
      onValueChange = { formState.unit.update(it) },
      label = {
        Text(
          text = stringResource(R.string.label_unit).asOptionalLabel(),
          softWrap = false,
          fontSize = MaterialTheme.typography.bodyLarge.fontSize / 1.5
        )
      },
      shape = RectangleShape,
      colors = borderlessTextFieldColors(),
      keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
      ),
      textStyle = MaterialTheme.typography.bodyMedium,
      singleLine = true,
      modifier = Modifier.weight(1f)
    )
    IconButton(onClick = onDelete) {
      Icon(
        painter = painterResource(R.drawable.rounded_delete_24),
        contentDescription = stringResource(R.string.cd_delete_chapter),
        tint = MaterialTheme.colorScheme.error
      )
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    BackgroundSurface {
      RecipeChapterScreen(
        index = 0,
        formState = RecipeFormViewModel.FormChapterState(onChange = {}).apply {
          name.update("Chapter 1")
          note.update("This is a note")
          steps.add(
            RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange).apply {
              description.update("This is a description")
              timerMinutes.update(4)
              note.update("This is a note")
              ingredients.add(
                RecipeFormViewModel.FormIngredientState(
                id = UUID.randomUUID(), onChange
              ).apply {
                name.update("Ingredient 1")
                amount.update(20.0)
                unit.update("g")
              }, RecipeFormViewModel.FormIngredientState(id = UUID.randomUUID(), onChange).apply {
                name.update("Ingredient 2")
                amount.update(200.0)
                unit.update("mg")
              })
            },
            RecipeFormViewModel.FormStepState(id = UUID.randomUUID(), onChange),
          )
        },
        onDelete = {},
        onMoveLeft = {},
        onMoveRight = {})
    }
  }
}