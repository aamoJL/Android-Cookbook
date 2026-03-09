package com.aamo.cookbook.features.recipe.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.ui.theme.CookbookTheme

@Composable
fun RecipeList(
  recipes: List<RecipeListRecipeModel>, onRecipeSelected: (Recipe) -> Unit
) {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(150.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    contentPadding = PaddingValues(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    items(recipes) { recipe ->
      RecipeCard(
        recipe = recipe.recipe,
        onClick = { onRecipeSelected(recipe.recipe) },
        isBookmarked = recipe.isBookmarked,
        rating = recipe.rating,
        modifier = Modifier
          .fillMaxSize()
          .aspectRatio(1f)
      )
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview(showBackground = true)
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeList(
      recipes = listOf(
        RecipeListRecipeModel(
          recipe = Recipe(id = 1, name = "Recipe 1"), rating = null, isBookmarked = true
        ),
        RecipeListRecipeModel(
          recipe = Recipe(id = 2, name = "Recipe 2"), rating = null, isBookmarked = true
        ),
      ),
      onRecipeSelected = {},
    )
  }
}