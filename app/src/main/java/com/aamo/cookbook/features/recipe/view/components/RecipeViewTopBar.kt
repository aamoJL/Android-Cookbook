package com.aamo.cookbook.features.recipe.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun RecipeViewTopBar(
  title: String,
  isBookmarked: Boolean,
  onEdit: () -> Unit,
  onCopy: () -> Unit,
  onUpdateBookmark: (Boolean) -> Unit,
  onOpenCalculator: () -> Unit,
  onOpenTimer: () -> Unit,
  onBack: () -> Unit
) {
  var openMenuDropDown by remember { mutableStateOf(false) }

  PrimaryTopAppBar(title = title, onBack = onBack) {
    IconButton(onClick = onOpenCalculator) {
      Icon(
        painterResource(R.drawable.baseline_calculate_24),
        contentDescription = stringResource(R.string.cd_open_calculator)
      )
    }
    IconButton(onClick = onOpenTimer) {
      Icon(
        painterResource(R.drawable.baseline_alarm_24),
        contentDescription = stringResource(R.string.cd_open_timer)
      )
    }
    Box(modifier = Modifier) {
      IconButton(onClick = { openMenuDropDown = !openMenuDropDown }) {
        Icon(
          painter = painterResource(R.drawable.rounded_more_vert_24),
          contentDescription = stringResource(R.string.cd_more_options)
        )
      }
      DropdownMenu(
        expanded = openMenuDropDown, onDismissRequest = { openMenuDropDown = false }) {
        DropdownMenuItem(leadingIcon = {
          Icon(
            painter = painterResource(R.drawable.rounded_edit_24),
            contentDescription = stringResource(R.string.cd_edit_recipe)
          )
        }, text = { Text(text = stringResource(R.string.cd_edit_recipe)) }, onClick = {
          openMenuDropDown = false
          onEdit()
        })
        DropdownMenuItem(leadingIcon = {
          Icon(
            painter = painterResource(id = R.drawable.baseline_content_copy_24),
            contentDescription = stringResource(R.string.cd_copy_recipe)
          )
        }, text = { Text(text = stringResource(R.string.cd_copy_recipe)) }, onClick = {
          openMenuDropDown = false
          onCopy()
        })
        HorizontalDivider()
        DropdownMenuItem(leadingIcon = {
          if (isBookmarked) {
            Icon(
              painter = painterResource(R.drawable.rounded_bookmark_remove_24),
              contentDescription = stringResource(R.string.cd_remove_bookmark)
            )
          }
          else {
            Icon(
              painter = painterResource(R.drawable.rounded_bookmark_add_24px),
              contentDescription = stringResource(R.string.cd_add_bookmark)
            )
          }
        }, text = {
          if (isBookmarked) Text(text = stringResource(R.string.button_text_remove_from_favorites))
          else Text(text = stringResource(R.string.button_text_add_to_favorites))
        }, onClick = {
          openMenuDropDown = false
          onUpdateBookmark(!isBookmarked)
        })
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun RecipeViewTopBarPreview() {
  CookbookTheme {
    RecipeViewTopBar(
      title = "Title",
      isBookmarked = false,
      onEdit = { },
      onCopy = { },
      onUpdateBookmark = { },
      onOpenCalculator = { },
      onOpenTimer = { },
      onBack = { })
  }
}