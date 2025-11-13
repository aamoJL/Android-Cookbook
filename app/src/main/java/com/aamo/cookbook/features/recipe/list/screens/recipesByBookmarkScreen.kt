package com.aamo.cookbook.features.recipe.list.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.components.RecipeCard
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.use_cases.fetchRecipes
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.extensions.general.ifElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object RecipesByBookmarkScreen

class RecipesByBookmarkScreenViewModel(
  fetchData: () -> Flow<List<RecipeListRecipeModel>>,
) : ViewModel() {
  private var _recipes = MutableStateFlow<List<RecipeListRecipeModel>>(emptyList())
  private var _categoryFilter = MutableStateFlow(String.EMPTY)

  val recipes = combine(_recipes, _categoryFilter) { recipe, word ->
    recipe.filter { it.recipe.subCategory.contains(word, ignoreCase = true) }
  }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

  val categories = _recipes.map { list ->
    list.map { it.recipe.subCategory }.distinct().filter { it.isNotEmpty() }
  }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

  val categoryFilter = _categoryFilter.asStateFlow()

  var isLoading by mutableStateOf(true)
    private set

  init {
    viewModelScope.launch {
      fetchData().collect { result ->
        _recipes.update { result }
        isLoading = false
      }
    }
  }

  fun updateFilter(value: String) {
    _categoryFilter.update { value }
  }
}

fun NavGraphBuilder.recipesByBookmarkScreen(
  onOpenRecipe: (id: Long) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenRecipeForm: () -> Unit,
  onBack: () -> Unit
) {
  composable<RecipesByBookmarkScreen> {
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipesByBookmarkScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipesByBookmarkScreenViewModel(
          fetchData = {
            fetchRecipes { dao.getBookmarksWithRatingFlow() }
          })
      }
    })
    val recipes by viewmodel.recipes.collectAsStateWithLifecycle()
    val categories by viewmodel.categories.collectAsStateWithLifecycle()
    val filter by viewmodel.categoryFilter.collectAsStateWithLifecycle()

    LoadingScreen(loading = viewmodel.isLoading) {
      RecipesByBookmarkScreenContent(
        recipes = recipes,
        categories = categories,
        filtered = filter.isNotEmpty(),
        onFilterChange = { viewmodel.updateFilter(it) },
        onRecipeSelected = { onOpenRecipe(it.id) },
        onBack = onBack,
        onSearch = onOpenSearch,
        onAdd = onOpenRecipeForm
      )
    }
  }
}

@Composable
private fun RecipesByBookmarkScreenContent(
  recipes: List<RecipeListRecipeModel>,
  categories: List<String>,
  filtered: Boolean,
  onFilterChange: (String) -> Unit,
  onRecipeSelected: (Recipe) -> Unit,
  onBack: () -> Unit,
  onSearch: () -> Unit,
  onAdd: () -> Unit,
) {
  var filterPopUpOpen by remember { mutableStateOf(false) }

  Scaffold(topBar = {
    PrimaryTopAppBar(
      title = stringResource(R.string.screen_title_bookmarks), actions = {
        IconButton(onClick = onSearch) {
          Icon(
            painter = painterResource(R.drawable.rounded_search_24),
            contentDescription = stringResource(R.string.description_search)
          )
        }
        IconButton(onClick = onAdd) {
          Icon(
            painter = painterResource(R.drawable.rounded_add_24),
            contentDescription = stringResource(R.string.description_add_new_recipe)
          )
        }
      }, onBack = onBack
    )
  }, floatingActionButton = {
    if (categories.isNotEmpty()) {
      Box {
        FloatingActionButton(onClick = { filterPopUpOpen = true }) {
          Icon(
            painter = ifElse(condition = filtered, ifTrue = {
              painterResource(R.drawable.baseline_filter_alt_off_24)
            }, ifFalse = {
              painterResource(R.drawable.baseline_filter_list_alt_24)
            }), contentDescription = stringResource(R.string.description_filter)
          )
        }
        DropdownMenu(
          expanded = filterPopUpOpen, onDismissRequest = { filterPopUpOpen = false }) {
          Column {
            categories.forEach { subCategory ->
              DropdownMenuItem(text = { Text(text = subCategory) }, onClick = {
                filterPopUpOpen = false
                onFilterChange(subCategory)
              })
            }
          }
          if (filtered) {
            HorizontalDivider()
            DropdownMenuItem(text = {
              Text(
                text = stringResource(R.string.button_text_clear),
                color = MaterialTheme.colorScheme.error
              )
            }, onClick = {
              filterPopUpOpen = false
              onFilterChange(String.EMPTY)
            })
          }
        }
      }
    }
  }) {
    Surface(modifier = Modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        items(recipes) { recipe ->
          RecipeCard(
            recipe = recipe.recipe,
            onClick = { onRecipeSelected(recipe.recipe) },
            isBookmarked = recipe.isBookmarked,
            rating = recipe.rating,
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp)
          )
        }
      }
    }
  }
}