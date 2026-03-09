package com.aamo.cookbook.features.recipe.list.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.features.recipe.list.components.RecipeList
import com.aamo.cookbook.features.recipe.list.models.RecipeListRecipeModel
import com.aamo.cookbook.features.recipe.list.use_cases.fetchRecipesByCategory
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.theme.CookbookTheme
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
import kotlinx.serialization.Serializable

@Serializable
data class RecipesByCategoryScreen(val category: String)

class RecipesByCategoryScreenViewModel(
  fetchData: () -> Flow<List<RecipeListRecipeModel>>,
) : ViewModel() {
  private var _subCategoryFilter = MutableStateFlow(String.EMPTY)
  val subCategoryFilter = _subCategoryFilter.asStateFlow()

  val recipes = combine(fetchData(), _subCategoryFilter) { recipe, word ->
    recipe.filter { it.recipe.subCategory.contains(word, ignoreCase = true) }
  }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

  val subCategories = fetchData().map { list ->
    list.map { it.recipe.subCategory }.distinct().filter { it.isNotEmpty() }
  }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

  fun updateFilter(value: String) {
    _subCategoryFilter.update { value }
  }
}

fun NavGraphBuilder.recipesByCategoryScreen(
  onOpenRecipe: (id: Long) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenRecipeForm: () -> Unit,
  onBack: () -> Unit
) {
  composable<RecipesByCategoryScreen> { navStack ->
    val (category) = navStack.toRoute<RecipesByCategoryScreen>()
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipesByCategoryScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipesByCategoryScreenViewModel(
          fetchData = { fetchRecipesByCategory(dao = dao, category = category) })
      }
    })
    val recipes by viewmodel.recipes.collectAsStateWithLifecycle()
    val subCategories by viewmodel.subCategories.collectAsStateWithLifecycle()
    val filter by viewmodel.subCategoryFilter.collectAsStateWithLifecycle()

    LoadingScreen(loading = recipes == null) {
      RecipesByCategoryScreenContent(
        title = category,
        recipes = checkNotNull(recipes),
        subCategories = subCategories,
        filtered = filter.isNotEmpty(),
        onFilterChange = { viewmodel.updateFilter(it) },
        onRecipeSelected = { onOpenRecipe(it.id) },
        onBack = onBack,
        onSearch = onOpenSearch,
        onAdd = onOpenRecipeForm,
      )
    }
  }
}

@Composable
private fun RecipesByCategoryScreenContent(
  title: String,
  recipes: List<RecipeListRecipeModel>,
  subCategories: List<String>,
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
      title = title,
      onBack = onBack,
    ) {
      IconButton(onClick = onSearch) {
        Icon(
          painter = painterResource(R.drawable.rounded_search_24),
          contentDescription = stringResource(R.string.cd_search)
        )
      }
      IconButton(onClick = onAdd) {
        Icon(
          painter = painterResource(R.drawable.rounded_add_24),
          contentDescription = stringResource(R.string.cd_add_new_recipe)
        )
      }
    }
  }, floatingActionButton = {
    if (subCategories.isNotEmpty()) {
      Box {
        FloatingActionButton(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          onClick = { filterPopUpOpen = true }) {
          Icon(
            painter = ifElse(condition = filtered, ifTrue = {
            painterResource(R.drawable.baseline_filter_alt_off_24)
          }, ifFalse = {
            painterResource(R.drawable.baseline_filter_list_alt_24)
          }), contentDescription = stringResource(R.string.cd_filter))
        }
        DropdownMenu(
          expanded = filterPopUpOpen,
          containerColor = MaterialTheme.colorScheme.surface,
          onDismissRequest = { filterPopUpOpen = false }) {
          Column {
            subCategories.forEach { subCategory ->
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
                text = stringResource(R.string.btn_text_clear),
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
    BackgroundSurface(
      modifier = Modifier
        .padding(it)
        .fillMaxSize()
    ) {
      RecipeList(recipes = recipes, onRecipeSelected = onRecipeSelected)
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipesByCategoryScreenContent(
      title = "Title",
      recipes = listOf(
        RecipeListRecipeModel(
          recipe = Recipe(name = "Recipe 1"), rating = 3, isBookmarked = false
        ),
        RecipeListRecipeModel(
          recipe = Recipe(name = "Recipe 1"), rating = null, isBookmarked = false
        ),
        RecipeListRecipeModel(
          recipe = Recipe(name = "Recipe 1"), rating = 5, isBookmarked = true
        ),
      ),
      subCategories = listOf("asd"),
      filtered = false,
      onFilterChange = {},
      onRecipeSelected = {},
      onBack = {},
      onSearch = {},
      onAdd = {})
  }
}