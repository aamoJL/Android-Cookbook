package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.ui.components.BasicTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
  categories: List<String>,
  onSelect: (String) -> Unit,
  onAddClick: () -> Unit,
) {
  Scaffold(
    topBar = { BasicTopAppBar(title = "Valitse kategoria") },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          onAddClick()
        }) {
        Icon(Icons.Filled.Add, "Lisää resepti nappi")
      }
    }
  ) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
      CategoryList(
        categories = categories,
        onSelect = {
          onSelect(it)
        })
    }
  }
}

@Composable
private fun CategoryList(categories: List<String>, onSelect: (String) -> Unit) {
  Column {
    Divider(color = MaterialTheme.colorScheme.secondary)
    LazyColumn {
      items(categories) { category ->
        CategoryItem(
          category = category,
          onClick = { onSelect(category) },
          modifier = Modifier.fillMaxWidth()
        )
        Divider(color = MaterialTheme.colorScheme.secondary)
      }
    }
  }
}

@Composable
private fun CategoryItem(
  category: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.clickable(onClick = onClick)) {
    Text(
      text = (if (category != "") category else "Muut"),
      style = MaterialTheme.typography.titleLarge,
      modifier = modifier.padding(20.dp, 40.dp, 20.dp, 40.dp)
    )
  }
}


