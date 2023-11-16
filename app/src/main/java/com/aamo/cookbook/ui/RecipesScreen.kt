package com.aamo.cookbook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe

@Composable
fun RecipesScreen(
  recipes: List<Recipe>,
  onSelect: (Recipe) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier){
    Column(){
      Divider(color = MaterialTheme.colorScheme.secondary)
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)){
        items(recipes){recipe ->
          RecipeItem(
            recipe = recipe,
            onClick = { onSelect(recipe) },
            Modifier.fillMaxWidth())
        }
      }
    }
  }
}

@Composable fun RecipeItem(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(modifier = modifier
    .clickable(onClick = onClick)
    .height(100.dp)) {
    Box(modifier = Modifier.fillMaxSize()){
      Text(text = recipe.name, textAlign = TextAlign.Center, modifier = Modifier
        .padding(8.dp)
        .align(Alignment.Center))
    }
  }
}