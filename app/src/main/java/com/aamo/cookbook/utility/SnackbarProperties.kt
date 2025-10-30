package com.aamo.cookbook.utility

import androidx.compose.material3.SnackbarDuration

data class SnackbarProperties(
  val message: String,
  val actionLabel: String? = null,
  val withDismissAction: Boolean = false,
  val duration: SnackbarDuration = SnackbarDuration.Short
)