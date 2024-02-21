package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aamo.cookbook.R
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import com.aamo.cookbook.ui.components.RecipeCard
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.viewModel.RecipeSearchViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider

@Composable
fun RecipeSearchScreen(
  viewModel: RecipeSearchViewModel = viewModel(factory = ViewModelProvider.Factory),
  onSelect: (id: Int) -> Unit = {},
  onBack: () -> Unit = {},
) {
  val searchWord by viewModel.searchWord.collectAsState()
  val recipes by viewModel.validRecipes.collectAsState()

  RecipeSearchScreenContent(
    onSelect = onSelect,
    onBack = onBack,
    searchWord = searchWord,
    setSearchWord = { viewModel.setSearchWord(it) },
    recipes = recipes
  )
}

@Composable
fun RecipeSearchScreenContent(
  recipes: List<RecipeWithFavoriteAndRating>,
  searchWord: String = "",
  onSelect: (id: Int) -> Unit = {},
  onBack: () -> Unit = {},
  setSearchWord: (String) -> Unit = {},
) {
  Scaffold(
    topBar = {
      SearchTopBar(
        value = searchWord,
        onValueChange = { setSearchWord(it) },
        onBack = onBack
      )
    }
  ) {
    Surface(Modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        items(recipes) { recipe ->
          RecipeCard(
            recipe = recipe.value,
            onClick = { onSelect(recipe.value.id) },
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
              .testTag(Tags.RECIPE_ITEM.name),
            isFavorite = recipe.favorite != null,
            rating = recipe.rating?.ratingOutOfFive ?: 0
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
  value: String,
  onValueChange: (String) -> Unit,
  onBack: () -> Unit
) {
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(true){
    focusRequester.requestFocus()
  }

  TopAppBar(
    title = {
      TopBarTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.focusRequester(focusRequester)
      )
    },
    colors = TopAppBarDefaults.topAppBarColors(
      actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.primaryContainer,
    ),
    navigationIcon = {
      IconButton(onClick = {
        onBack()
      }) {
        Icon(
          Icons.Filled.ArrowBack,
          contentDescription = stringResource(R.string.description_screen_back)
        )
      }
    }
  )
}

@Composable
private fun TopBarTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  TextField(
    value = value,
    onValueChange = onValueChange,
    placeholder = { Text(stringResource(R.string.textfield_placeholder_search)) },
    leadingIcon = {
      Icon(
        imageVector = Icons.Filled.Search,
        contentDescription = stringResource(R.string.description_search)
      )
    },
    trailingIcon = {
      IconButton(onClick = {  }) {
        Icon(
          imageVector = Icons.Filled.Clear,
          contentDescription = "Clear"
        )
      }
    },
    shape = RectangleShape,
    singleLine = true,
    keyboardOptions = KeyboardOptions.Default.copy(
      capitalization = KeyboardCapitalization.Sentences,
      keyboardType = KeyboardType.Text
    ),
    modifier = modifier
  )
}

@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeSearchScreenContent(
      recipes = listOf(
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), FavoriteRecipe(recipeId = 0), null),
        RecipeWithFavoriteAndRating(
          Recipe(name = "Resepti 1"),
          null,
          RecipeRating(ratingOutOfFive = 3, recipeId = 0)
        ),
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), null, null),
        RecipeWithFavoriteAndRating(Recipe(name = "Resepti 1"), FavoriteRecipe(recipeId = 0), null)
      )
    )
  }
}