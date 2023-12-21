package com.aamo.cookbook.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.FormNumberField
import com.aamo.cookbook.ui.components.FormTextField

class EditRecipeScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditChapter: (index: Int) -> Unit,
    modifier: Modifier = Modifier)
  {
    val uiState by viewModel.infoUiState.collectAsState()

    Column(modifier = modifier
      .fillMaxSize()
      .padding(4.dp)) {
      Text(text = "Uusi resepti", style = MaterialTheme.typography.titleLarge)
      // Info
      Column(modifier = Modifier.fillMaxWidth()) {
        FormTextField(
          value = uiState.name,
          onValueChange = { viewModel.setRecipeName(it) },
          label = "Reseptin nimi",
        )
        FormTextField(
          value = uiState.category,
          onValueChange = { viewModel.setCategory(it) },
          label = "Kategoria"
        )
        FormTextField(
          value = uiState.subCategory,
          onValueChange = { viewModel.setSubCategory(it) },
          label = "Alakategoria (valinnainen)"
        )
        FormNumberField(
          value = uiState.servings,
          onValueChange = { viewModel.setServings(it ?: 1) },
          label = "Annosten määrä")
      }
      // Chapters
      Text(text = "Kappaleet", style = MaterialTheme.typography.titleLarge)
      Button(onClick = { onEditChapter(-1) }) {
        Text(text = "Lisää uusi kappale")
      }
    }
  }
}
