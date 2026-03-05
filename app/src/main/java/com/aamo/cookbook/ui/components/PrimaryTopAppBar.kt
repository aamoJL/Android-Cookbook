package com.aamo.cookbook.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.inputs.BackNavigationIconButton
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.tags.UITag

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PrimaryTopAppBar(
  title: String,
  modifier: Modifier = Modifier,
  onBack: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  TopAppBar(
    title = {
      Text(text = title, Modifier.testTag(UITag.PAGE_TITLE.name))
    }, colors = TopAppBarDefaults.topAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
      navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary,
    ), navigationIcon = {
      if (onBack != null) {
        BackNavigationIconButton(onBack = onBack)
      }
    }, actions = actions, modifier = modifier.shadow(elevation = 4.dp, shape = RectangleShape)
  )
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun Preview() {
  CookbookTheme() {
    PrimaryTopAppBar(title = "Title", onBack = {}) {
      IconButton(onClick = {}) {
        Icon(
          painter = painterResource(R.drawable.rounded_search_24), contentDescription = null
        )
      }
    }
  }
}