package com.aamo.cookbook.ui.components

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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BasicTopAppBar(
  title: String,
  backEnabled: Boolean = true,
  onBack: () -> Unit = {}
) {
  TopAppBar(
    title = {
      Text(text = title)
    },
    colors = TopAppBarDefaults.smallTopAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.primaryContainer,
    ),
    navigationIcon = {
      if (backEnabled) {
        IconButton(onClick = {
          onBack()
        }) {
          Icon(Icons.Filled.ArrowBack, contentDescription = "Takaisin")
        }
      }
    }
  )
}