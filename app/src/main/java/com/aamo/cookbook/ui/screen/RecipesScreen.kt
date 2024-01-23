package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.utility.Tags

@Composable
fun RecipesScreen(
  recipes: List<Recipe>,
  onSelectRecipe: (Recipe) -> Unit,
  onBack: () -> Unit,
  onSearch: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val subCategories by remember(recipes) { mutableStateOf(recipes.map { it.subCategory }.distinct()) }
  var filterValue by remember { mutableStateOf<String?>(null) }
  var filterPopUpOpen by remember { mutableStateOf(false) }
  val filteredRecipes by remember(filterValue, subCategories) { mutableStateOf(
    if(filterValue == null) recipes else recipes.filter { it.subCategory == filterValue }
  ) }

  Scaffold(
    topBar = {
      BasicTopAppBar(
        title = recipes.elementAtOrNull(0)?.category ?: stringResource(R.string.screen_title_error_recipes_not_found),
        actions = {
          IconButton(onClick = onSearch) {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = stringResource(R.string.description_search)
            )
          }
        },
        onBack = onBack)
    },
    floatingActionButton = {
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
  ) {
    Surface(modifier = modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        items(filteredRecipes) { recipe ->
          RecipeItem(
            recipe = recipe,
            onClick = { onSelectRecipe(recipe) },
            Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}

@Composable
private fun RecipeItem(recipe: Recipe, onClick: () -> Unit, modifier: Modifier = Modifier) {
  ElevatedCard(
    shape = RectangleShape,
    modifier = modifier
      .clickable(onClick = onClick)
      .height(200.dp)
      .testTag(Tags.RECIPE_ITEM.name)
  ) {
    Column {
      // TODO: Image
      Spacer(modifier = Modifier.weight(1f, true))
      Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
          Text(
            text = recipe.name,
            style = MaterialTheme.typography.titleMedium
          )
        }
      }
    }
  }
}
