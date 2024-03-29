package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.RecipeCard
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags

@Composable
fun RecipesScreen(
  title: String,
  recipes: List<RecipeWithFavoriteAndRating>,
  onSelectRecipe: (Recipe) -> Unit,
  onBack: () -> Unit,
  onSearch: () -> Unit,
  onAdd: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val subCategories by remember(recipes) { mutableStateOf(recipes.map { it.value.subCategory }.distinct()) }
  var filterValue by remember { mutableStateOf<String?>(null) }
  var filterPopUpOpen by remember { mutableStateOf(false) }
  val filteredRecipes by remember(filterValue, subCategories) { mutableStateOf(
    if(filterValue == null) recipes else recipes.filter { it.value.subCategory == filterValue }
  ) }

  Scaffold(
    topBar = {
      BasicTopAppBar(
        title = title,
        actions = {
          IconButton(onClick = onSearch) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = stringResource(R.string.description_search)
            )
          }
          IconButton(onClick = onAdd) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = stringResource(R.string.description_add_new_recipe)
            )
          }
        },
        onBack = onBack)
    },
    floatingActionButton = {
      if(subCategories.dropWhile { it.isEmpty() }.isNotEmpty()){
        Box {
          FloatingActionButton(onClick = { filterPopUpOpen = true }) {
            Icon(
              painter = when(filterValue) {
                null -> painterResource(R.drawable.baseline_filter_list_alt_24)
                else -> painterResource(R.drawable.baseline_filter_alt_off_24)
              },
              contentDescription = stringResource(R.string.description_filter)
            )
          }
          DropdownMenu(
            expanded = filterPopUpOpen,
            onDismissRequest = { filterPopUpOpen = false }
          ) {
            Column {
              subCategories.forEach { subCategory ->
                DropdownMenuItem(
                  text = { Text(text = subCategory)},
                  onClick = {
                    filterPopUpOpen = false
                    filterValue = subCategory
                  })
              }
            }
            if(filterValue != null){
              Divider()
              DropdownMenuItem(
                text = { Text(text = "Reset", color = MaterialTheme.colorScheme.error)},
                onClick = {
                  filterPopUpOpen = false
                  filterValue = null
                })
            }
          }
        }
      }
    }
  ) {
    Surface(modifier = modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        items(filteredRecipes) { recipe ->
          RecipeCard(
            recipe = recipe.value,
            onClick = { onSelectRecipe(recipe.value) },
            isFavorite = recipe.favorite != null,
            rating = recipe.rating?.ratingOutOfFive ?: 0,
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .testTag(Tags.RECIPE_ITEM.name),
          )
        }
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipesScreen(
      title = "Title",
      recipes = listOf(
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), FavoriteRecipe(recipeId = 0), null),
        RecipeWithFavoriteAndRating(
          Recipe(name = "Resepti 1"),
          null,
          RecipeRating(ratingOutOfFive = 3, recipeId = 0)
        ),
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), null, null),
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), FavoriteRecipe(recipeId = 0), null)
      ),
      onSelectRecipe = {},
      onBack = {},
      onSearch = {},
      onAdd = {})
  }
}
