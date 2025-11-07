package com.aamo.cookbook.utility.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDismissibleItem(
  dismissAction: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable (RowScope.() -> Unit),
) {
  val positionalThreshold = with(LocalDensity.current) { 150.dp.toPx() }
  val dismissState = rememberSwipeToDismissBoxState(positionalThreshold = { positionalThreshold })

  SwipeToDismissBox(
    state = dismissState,
    backgroundContent = { DismissBackground(dismissState) },
    enableDismissFromEndToStart = false,
    enableDismissFromStartToEnd = true,
    onDismiss = { dir -> if (dir == SwipeToDismissBoxValue.StartToEnd) dismissAction() },
    content = content,
    modifier = modifier
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
  val color = when (dismissState.dismissDirection) {
    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primaryContainer
    else -> Color.Transparent
  }

  val direction = dismissState.dismissDirection

  Row(
    modifier = Modifier
      .fillMaxSize()
      .background(color)
      .padding(12.dp, 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    if (direction == SwipeToDismissBoxValue.StartToEnd) Icon(
      painter = painterResource(R.drawable.rounded_delete_sweep_24),
      contentDescription = stringResource(R.string.description_delete_list_item)
    )
  }
}