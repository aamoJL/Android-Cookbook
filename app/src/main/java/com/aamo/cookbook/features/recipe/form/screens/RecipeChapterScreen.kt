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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
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
import com.aamo.cookbook.features.recipe.form.models.states.FormChapterState
import com.aamo.cookbook.features.recipe.form.models.states.FormIngredientState
import com.aamo.cookbook.features.recipe.form.models.states.FormStepState
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
  formState: FormChapterState,
  onDelete: () -> Unit,
  onMoveLeft: (() -> Unit)?,
  onMoveRight: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
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
        ChapterForm(
          state = formState,
          onDelete = onDelete,
          onMoveLeft = onMoveLeft,
          onMoveRight = onMoveRight,
        )
        HorizontalDividerLabel(
          label = stringResource(R.string.title_steps), modifier = Modifier.padding(12.dp)
        )
      }
    }
    itemsIndexed(items = formState.stepStates.values, key = { _, x -> x.guid }) { i, step ->
      if (formState.selectedStepId == step.guid) {
        StepForm(
          state = step,
          onMoveUp = ifElse(
            condition = i == 0,
            ifTrue = { null },
            ifFalse = { { formState.stepStates.swapAt(i, i - 1) } }),
          onMoveDown = ifElse(
            condition = i == formState.stepStates.values.size - 1,
            ifTrue = { null },
            ifFalse = { { formState.stepStates.swapAt(i, i + 1) } }),
          onDelete = { formState.stepStates.removeAt(i) },
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
            ifFalse = { { formState.stepStates.swapAt(i, i - 1) } }),
          onMoveDown = ifElse(
            condition = i == formState.stepStates.values.size - 1,
            ifTrue = { null },
            ifFalse = { { formState.stepStates.swapAt(i, i + 1) } }),
          modifier = Modifier
            .clickable { formState.selectedStepId = step.guid }
            .animateItem()
            .padding(bottom = 8.dp))
      }
    }
    item {
      Box(modifier = Modifier.padding(top = 8.dp)) {
        FilledIconButton(
          onClick = {
            formState.addStep().also {
              formState.selectedStepId = it.guid // Select the added step
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
fun ChapterForm(
  state: FormChapterState,
  onDelete: () -> Unit,
  onMoveLeft: (() -> Unit)?,
  onMoveRight: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  var openDeleteDialog by rememberSaveable { mutableStateOf(false) }
  // Textfield invokes focus change on init.
  // This prevents the field error message showing before user has interacted with it.
  var focusInit by remember { mutableStateOf(true) }
  var hasFocus by remember { mutableStateOf(false) }

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_chapter),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onDelete()
    },
  )

  ElevatedCard(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ),
    modifier = modifier,
  ) {
    Column {
      ChapterFormFields(
        fields = state.fields,
        modifier = Modifier
          .animateContentSize()
          .padding(8.dp)
          .padding(bottom = 4.dp)
          .fillMaxWidth()
          .onFocusChanged {
            if (it.hasFocus) hasFocus = it.hasFocus
            if (focusInit) focusInit = false
            else hasFocus = it.hasFocus
          },
      )
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
              enabled = state.fields.note.value.isEmpty(),
              checked = state.fields.noteFieldToggleValue,
              onCheckedChange = { state.fields.noteFieldToggleValue = it },
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
      if (state.fields.isDirty && !state.fields.requiredFieldsFilled && !hasFocus) {
        Surface(
          color = MaterialTheme.colorScheme.errorContainer,
          contentColor = MaterialTheme.colorScheme.onErrorContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = stringResource(R.string.text_required_fields_are_not_filled),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(2.dp),
          )
        }
      }
    }
  }
}

@Composable
fun ChapterFormFields(fields: FormChapterState.Fields, modifier: Modifier = Modifier) {
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier,
  ) {
    TextField(
      value = fields.name.value,
      onValueChange = { fields.name.update(it) },
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
    if (fields.noteFieldToggleValue) {
      TextField(
        value = fields.note.value,
        onValueChange = { fields.note.update(it) },
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
}

@Composable
private fun StepDisplay(
  formState: FormStepState,
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
              if (formState.fields.timerMinutes.value.let { it != null && it > 0 }) {
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
                      R.string.abbreviation_minutes, formState.fields.timerMinutes.value.toString()
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
            if (formState.fields.description.value.isEmpty()) {
              Text(
                text = stringResource(R.string.text_click_to_edit_step),
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.titleMedium,
              )
            }
            else {
              Text(
                text = formState.fields.description.value,
                style = MaterialTheme.typography.titleMedium
              )
            }
            if (formState.fields.note.value.isNotEmpty()) {
              NoteCard(
                text = formState.fields.note.value,
                label = String.EMPTY,
                modifier = Modifier.fillMaxWidth()
              )
            }
            if (formState.ingredientStates.values.isNotEmpty()) {
              Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.extraSmall,
              ) {
                IngredientList(
                  fields = formState.ingredientStates.values.map { it.fields },
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
        if (!formState.validity.value) {
          Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = stringResource(R.string.text_required_fields_are_not_filled),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(2.dp),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun IngredientList(
  fields: List<FormIngredientState.Fields>,
  modifier: Modifier = Modifier,
  fontFamily: FontFamily = FontFamily.Default,
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
    if (fields.any { it.amount.value != Double.Zero }) {
      Column(modifier = Modifier.width(IntrinsicSize.Max)) {
        fields.forEach {
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
    if (fields.any { it.unit.value.isNotEmpty() }) {
      Column(modifier = Modifier.defaultMinSize(minWidth = 20.dp)) {
        fields.forEach {
          Text(text = it.unit.value, style = textStyle, fontFamily = fontFamily)
        }
      }
    }
    Column {
      fields.forEach {
        Text(text = it.name.value, style = textStyle, fontFamily = fontFamily)
      }
    }
  }
}

@Composable
private fun StepForm(
  state: FormStepState,
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
                enabled = state.fields.timerMinutes.value?.let { it > 0 } != true,
                checked = state.fields.timerFieldToggleValue,
                onCheckedChange = { state.fields.timerFieldToggleValue = it },
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
                enabled = state.fields.note.value.isEmpty(),
                checked = state.fields.noteFieldToggleValue,
                onCheckedChange = { state.fields.noteFieldToggleValue = it },
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
                  contentDescription = stringResource(R.string.cd_delete_step),
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
          VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
          StepFormFields(
            fields = state.fields,
            modifier = Modifier
              .weight(1f)
              .padding(8.dp)
              .padding(bottom = 4.dp)
              .fillMaxWidth(),
          )
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
            if (state.ingredientStates.values.isNotEmpty()) {
              HorizontalDividerLabel(
                label = stringResource(R.string.title_ingredients),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.ingredientStates.values.forEachIndexed { i, ingredient ->
                  IngredientFormFields(
                    fields = ingredient.fields,
                    onDelete = { state.ingredientStates.removeAt(i) },
                  )
                }
              }
            }
            Button(
              onClick = { state.addIngredient() },
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
private fun StepFormFields(fields: FormStepState.Fields, modifier: Modifier = Modifier) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
    TextField(
      value = fields.description.value,
      onValueChange = { fields.description.update(it) },
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
    if (fields.timerFieldToggleValue) {
      NumberField(
        value = fields.timerMinutes.value,
        onValueChange = { fields.timerMinutes.update(it) },
        validator = NullableIntFieldValidator,
        label = { Text(stringResource(R.string.label_step_timer).asOptionalLabel()) },
        shape = RectangleShape,
        suffix = { Text(text = stringResource(R.string.suffix_minutes)) },
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth()
      )
    }
    if (fields.noteFieldToggleValue) {
      TextField(
        value = fields.note.value,
        onValueChange = { fields.note.update(it) },
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
}

@Composable
private fun IngredientFormFields(
  fields: FormIngredientState.Fields,
  onDelete: () -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    TextField(
      value = fields.name.value,
      onValueChange = { fields.name.update(it) },
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
      value = fields.amount.value,
      onValueChange = { fields.amount.update(it) },
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
      value = fields.unit.value,
      onValueChange = { fields.unit.update(it) },
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
        contentDescription = stringResource(R.string.cd_delete_ingredient),
        tint = MaterialTheme.colorScheme.error
      )
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  val selectedId = UUID.randomUUID()

  CookbookTheme {
    BackgroundSurface {
      RecipeChapterScreen(
        index = 0,
        formState = FormChapterState(onValidityChanged = {}).apply {
          fields.apply {
            note.update("This is a note")
          }
          selectedStepId = selectedId
          stepStates.add(
            FormStepState(guid = UUID.randomUUID()) {}.apply {
              fields.apply {
                description.update("This is a description")
                timerMinutes.update(4)
                note.update("This is a note")
              }
              ingredientStates.add(FormIngredientState(guid = UUID.randomUUID()) {}.apply {
                fields.apply {
                  name.update("Ingredient 1")
                  amount.update(20.0)
                  unit.update("g")
                }
              }, FormIngredientState(guid = UUID.randomUUID()) {}.apply {
                fields.apply {
                  name.update("Ingredient 2")
                  amount.update(200.0)
                  unit.update("mg")
                }
              })
            },
            FormStepState(guid = UUID.randomUUID()) {},
            FormStepState(guid = selectedId) {},
          )
        },
        onDelete = {},
        onMoveLeft = {},
        onMoveRight = {},
      )
    }
  }
}