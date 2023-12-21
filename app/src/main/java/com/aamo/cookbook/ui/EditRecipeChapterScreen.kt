package com.aamo.cookbook.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.FormTextField

class EditRecipeChapterScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    chapterIndex: Int,
    onEditStep: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
  ) {
    val uiState by viewModel.chapterUiState.collectAsState()

    Column(modifier = modifier
      .fillMaxSize()
      .padding(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "1.", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 10.dp))
        FormTextField(
          value = uiState.name,
          onValueChange = { viewModel.setChapterName(it) },
          label = "Kappaleen nimi"
        )
      }
      // Steps
//      Text(text = "Vaiheet", style = MaterialTheme.typography.titleLarge)
//      Button(onClick = { onEditStep(chapter.steps.size) }) {
//        Text(text = "Lisää uusi vaihe")
//      }
    }
  }

}