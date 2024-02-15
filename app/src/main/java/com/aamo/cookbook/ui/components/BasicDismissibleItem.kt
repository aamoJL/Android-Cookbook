package com.aamo.cookbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDismissibleItem(
  dismissAction: () -> (Boolean),
  modifier: Modifier = Modifier,
  animationDuration: Int = 500,
  content: @Composable () -> Unit = {},
) {
  var isRemoved by remember { mutableStateOf(false) }
  val dismissState = rememberDismissState(
    confirmValueChange = { value ->
      when (value) {
        DismissValue.DismissedToEnd -> {
          isRemoved = true; true
        }

        else -> false
      }
    },
    positionalThreshold = { 150.dp.toPx() }
  )

  LaunchedEffect(isRemoved) {
    if (isRemoved) {
      delay(animationDuration.toLong()).also { dismissAction() }
    }
  }

  AnimatedVisibility(
    visible = !isRemoved,
    exit = shrinkVertically(
      animationSpec = tween(durationMillis = animationDuration),
      shrinkTowards = Alignment.Top
    ) + fadeOut()
  ) {
    SwipeToDismiss(
      state = dismissState,
      directions = setOf(DismissDirection.StartToEnd),
      modifier = modifier,
      background = { DismissBackground(dismissState) },
      dismissContent = {
        content()
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
  val color = when (dismissState.dismissDirection) {
    DismissDirection.StartToEnd -> MaterialTheme.colorScheme.errorContainer
    DismissDirection.EndToStart -> MaterialTheme.colorScheme.primaryContainer
    null -> Color.Transparent
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
    if (direction == DismissDirection.StartToEnd)
      Icon(
        Icons.Default.Delete,
        contentDescription = stringResource(R.string.description_delete_list_item)
      )
  }
}