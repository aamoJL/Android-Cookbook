package com.aamo.cookbook.ui.components.modals

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R

@Composable
fun DeleteDialog(open: Boolean, title: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
  if (!open) return

  AlertDialog(
    title = { Text(text = title) },
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = onConfirm,
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
      ) {
        Text(text = stringResource(R.string.btn_delete))
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.inversePrimary
        )
      ) {
        Text(text = stringResource(R.string.btn_cancel))
      }
    },
  )
}