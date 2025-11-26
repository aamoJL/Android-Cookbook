package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.aamo.cookbook.ui.components.inputs.BackNavigationIconButton
import com.aamo.cookbook.utility.tags.UITag

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PrimaryTopAppBar(
  title: String,
  onBack: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    title = {
      Text(text = title, Modifier.testTag(UITag.PAGE_TITLE.name))
    }, colors = TopAppBarDefaults.topAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.primaryContainer,
    ), navigationIcon = {
      if (onBack != null) {
        BackNavigationIconButton(onBack = onBack)
      }
    }, actions = actions
  )
}