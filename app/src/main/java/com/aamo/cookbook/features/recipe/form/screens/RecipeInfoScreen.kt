package com.aamo.cookbook.features.recipe.form.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.models.states.FormRecipeState
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.inputs.number_field.NullableIntFieldValidator
import com.aamo.cookbook.ui.components.inputs.number_field.NumberField
import com.aamo.cookbook.ui.components.inputs.text_field.OptionsTextField
import com.aamo.cookbook.ui.components.inputs.text_field.borderlessTextFieldColors
import com.aamo.cookbook.ui.components.modals.DeleteDialog
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.asOptionalLabel

@Composable
fun RecipeInfoScreen(
  formState: FormRecipeState,
  categorySuggestions: Map<String, List<String>>,
  onDelete: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  Column(modifier) {
    HorizontalDividerLabel(
      label = stringResource(R.string.title_recipe_info),
      modifier = Modifier.padding(12.dp),
    )
    RecipeForm(state = formState, categorySuggestions = categorySuggestions, onDelete = onDelete)
  }
}

@Composable
fun RecipeForm(
  state: FormRecipeState,
  categorySuggestions: Map<String, List<String>>,
  onDelete: (() -> Unit)?,
  modifier: Modifier = Modifier
) {
  var openDeleteDialog by rememberSaveable { mutableStateOf(false) }
  // Textfield invokes focus change on init.
  // This prevents the field error message showing before user has interacted with it.
  var focusInit by remember { mutableStateOf(true) }
  var hasFocus by remember { mutableStateOf(false) }

  DeleteDialog(
    open = openDeleteDialog,
    title = stringResource(R.string.dialog_title_delete_recipe),
    onDismiss = { openDeleteDialog = false },
    onConfirm = {
      openDeleteDialog = false
      onDelete?.invoke()
    },
  )

  ElevatedCard(
    shape = MaterialTheme.shapes.small,
    colors = CardDefaults.elevatedCardColors(
      containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    ),
    modifier = modifier,
  ) {
    Column {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .animateContentSize()
          .height(intrinsicSize = IntrinsicSize.Max)
      ) {
        Surface {
          Column(modifier = Modifier.fillMaxHeight()) {
            IconToggleButton(
              enabled = state.fields.note.value.isEmpty(),
              checked = state.fields.noteFieldToggleValue,
              onCheckedChange = { state.fields.noteFieldToggleValue = it },
              colors = IconButtonDefaults.iconToggleButtonColors(
                checkedContentColor = MaterialTheme.colorScheme.tertiary,
                disabledContentColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f)
              ),
            ) {
              Icon(
                painter = painterResource(R.drawable.sticky_note_2_24px),
                contentDescription = stringResource(R.string.cd_toggle_note_field)
              )
            }
            if (onDelete != null) {
              IconButton(onClick = { openDeleteDialog = true }) {
                Icon(
                  painter = painterResource(R.drawable.rounded_delete_24),
                  contentDescription = stringResource(R.string.cd_delete_recipe),
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
        VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f))
        RecipeFormFields(
          fields = state.fields,
          categorySuggestions = categorySuggestions,
          modifier = modifier
            .padding(8.dp)
            .padding(bottom = 4.dp)
            .weight(1f)
            .onFocusChanged {
              if (it.hasFocus) hasFocus = it.hasFocus
              if (focusInit) focusInit = false
              else hasFocus = it.hasFocus
            },
        )
      }
      if (state.fields.changed && !state.fields.requiredFieldsFilled && !hasFocus) {
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
private fun RecipeFormFields(
  fields: FormRecipeState.Fields,
  categorySuggestions: Map<String, List<String>>,
  modifier: Modifier = Modifier,
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        modifier = Modifier.weight(2f, true)
      )
      NumberField(
        value = fields.servings.value,
        onValueChange = { fields.servings.update(it) },
        validator = NullableIntFieldValidator,
        label = { Text(stringResource(R.string.label_servings)) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.weight(1f, true)
      )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
      OptionsTextField(
        value = fields.category.value,
        onValueChange = { fields.category.update(it) },
        options = categorySuggestions.keys.filter {
          it.contains(fields.category.value, ignoreCase = true)
        }.sorted(),
        label = { Text(stringResource(R.string.label_category), softWrap = false) },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next
        ),
        modifier = Modifier.weight(1f)
      )
      OptionsTextField(
        value = fields.subCategory.value,
        onValueChange = { fields.subCategory.update(it) },
        options = categorySuggestions[fields.category.value]?.filter {
          it.contains(fields.subCategory.value, ignoreCase = true)
        }?.sorted() ?: emptyList(),
        label = {
          Text(
            stringResource(R.string.label_subcategory).asOptionalLabel(), softWrap = false
          )
        },
        shape = RectangleShape,
        colors = borderlessTextFieldColors(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          capitalization = KeyboardCapitalization.Sentences,
          keyboardType = KeyboardType.Text,
          imeAction = if (fields.noteFieldToggleValue) ImeAction.Next else ImeAction.Done
        ),
        modifier = Modifier.weight(1f)
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

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    BackgroundSurface {
      RecipeInfoScreen(
        formState = FormRecipeState(onCanSaveChanged = {}).apply {
          fields.apply {
            name.update("Recipe 1")
            subCategory.update("Sub 1")
            note.update("This is a note")
          }
        },
        categorySuggestions = emptyMap(),
        onDelete = {},
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}