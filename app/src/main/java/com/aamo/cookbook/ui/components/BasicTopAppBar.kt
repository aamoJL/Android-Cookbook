package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R
import com.aamo.cookbook.utility.Tags

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BasicTopAppBar(
  title: String,
  onBack: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    title = {
      Text(text = title, Modifier.testTag(Tags.SCREEN_TITLE.name))
    },
    colors = TopAppBarDefaults.topAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.primaryContainer,
    ),
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = {
          onBack()
        }) {
          Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.description_screen_back))
        }
      }
    },
    actions = actions
  )
}