package com.aamo.cookbook.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun FormBase(
  title: String,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  ElevatedCard(modifier = modifier) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
      Row {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
      }
      content()
    }
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    FormBase(title = "Title") {
      FormTextField(
        value = "TextField",
        onValueChange = {},
        label = "Label",
      )
    }
  }
}