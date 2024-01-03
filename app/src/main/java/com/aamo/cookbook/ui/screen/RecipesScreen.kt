package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
  recipes: List<Recipe>,
  onSelect: (Recipe) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      BasicTopAppBar(
        title = recipes.elementAtOrNull(0)?.category ?: stringResource(R.string.screen_title_error_recipes_not_found),
        onBack = onBack)
    }
  ) {
    Surface(modifier = modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
      ) {
        items(recipes) { recipe ->
          RecipeItem(
            recipe = recipe,
            onClick = { onSelect(recipe) },
            Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
private fun RecipeItem(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
    color = MaterialTheme.colorScheme.secondaryContainer,
    modifier = modifier
      .clickable(onClick = onClick)
      .height(200.dp)
  ) {
    Box(modifier = Modifier) {
      Text(
        text = recipe.name,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .padding(8.dp)
          .align(Alignment.Center)
      )
    }
  }
}
