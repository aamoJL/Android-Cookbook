package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aamo.cookbook.R

@Composable
fun SaveButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(modifier = modifier) {
    Button(
      enabled = enabled,
      onClick = { onClick() },
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.Center)
    ) {
      Text(text = stringResource(R.string.button_text_save))
    }
  }
}