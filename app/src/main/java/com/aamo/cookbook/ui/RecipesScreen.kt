package com.aamo.cookbook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe

@Composable
fun RecipesScreen(
  recipes: List<Recipe>,
  onSelect: (Recipe) -> Unit,
  modifier: Modifier = Modifier.fillMaxSize()
) {
  Box(modifier = modifier){
    Column(){
      Divider(color = MaterialTheme.colorScheme.secondary)
      LazyColumn(){
        items(recipes){recipe ->
          RecipeItem(recipe = recipe, onClick = { onSelect(recipe) }, Modifier.fillMaxWidth())
          Divider(color = MaterialTheme.colorScheme.secondary)
        }
      }
    }
  }
}

@Composable fun RecipeItem(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(modifier = modifier.clickable(onClick = onClick)){
    Text(text = recipe.name, modifier.padding(16.dp))
  }
}