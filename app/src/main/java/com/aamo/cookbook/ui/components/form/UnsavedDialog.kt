package com.aamo.cookbook.ui.components.form

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R

@Composable
fun UnsavedDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    title = { Text(text = stringResource(R.string.dialog_title_unsaved_default)) },
    text = { Text(text = stringResource(R.string.dialog_text_unsaved_default)) },
    onDismissRequest = onDismiss,
    confirmButton =
    {
      TextButton(
        onClick = onConfirm
      ) {
        Text(stringResource(R.string.dialog_confirm_unsaved_default))
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss
      ) {
        Text(stringResource(R.string.dialog_dismiss_default))
      }
    })
}