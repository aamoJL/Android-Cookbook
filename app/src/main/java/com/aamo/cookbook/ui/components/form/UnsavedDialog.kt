package com.aamo.cookbook.ui.components.form

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun UnsavedDialog(
  onDismiss: () -> Unit,
  onConfirm: () -> Unit,
) {
  AlertDialog(
    title = { Text(text = "Tallentamattomia tietoja!") },
    text = { Text(text = "Muutoksesi menetetään, jos palaat edelliselle sivulle.") },
    onDismissRequest = onDismiss,
    confirmButton =
    {
      TextButton(
        onClick = onConfirm
      ) {
        Text("Älä tallenna")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss
      ) {
        Text("Peruuta")
      }
    })
}