package com.aamo.cookbook.features.recipe.list.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
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
import com.aamo.cookbook.ui.components.inputs.text_field.SearchTextField
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object RecipeSearchScreen

class RecipeSearchScreenViewModel(
  fetchData: () -> Flow<List<RecipeListRecipeModel>>,
) : ViewModel() {
  private var _recipes = MutableStateFlow<List<RecipeListRecipeModel>>(emptyList())
  private var _filterWord = MutableStateFlow(String.EMPTY)

  var isLoading by mutableStateOf(true)
    private set

  val recipes = combine(_recipes, _filterWord) { recipe, word ->
    recipe.filter { it.recipe.name.contains(word, ignoreCase = true) }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000L),
    initialValue = _recipes.value
  )

  val filterWord = _filterWord.asStateFlow()

  init {
    viewModelScope.launch {
      fetchData().collect { result ->
        _recipes.update { result }
        isLoading = false
      }
    }
  }

  fun updateFilter(value: String) {
    _filterWord.update { value }
  }
}

fun NavGraphBuilder.recipeSearchScreen(
  onOpenRecipe: (id: Long) -> Unit, onBack: () -> Unit
) {
  composable<RecipeSearchScreen> {
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipeSearchScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeSearchScreenViewModel(
          fetchData = {
            fetchRecipes { dao.getRecipesWithBookmarkAndRatingFlow() }
          })
      }
    })
    val recipes by viewmodel.recipes.collectAsStateWithLifecycle()
    val filterWord by viewmodel.filterWord.collectAsStateWithLifecycle()

    LoadingScreen(loading = viewmodel.isLoading) {
      RecipeSearchScreenContent(
        recipes = recipes,
        searchWord = filterWord,
        onSearchWordChange = { viewmodel.updateFilter(it) },
        onRecipeSelected = onOpenRecipe,
        onBack = onBack,
      )
    }
  }
}

@Composable
private fun RecipeSearchScreenContent(
  recipes: List<RecipeListRecipeModel>,
  searchWord: String,
  onRecipeSelected: (id: Long) -> Unit,
  onBack: () -> Unit,
  onSearchWordChange: (String) -> Unit,
) {
  Scaffold(
    topBar = {
      SearchTopBar(value = searchWord, onValueChange = onSearchWordChange, onBack = onBack)
    }) {
    Surface(Modifier.padding(it)) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(4.dp)
      ) {
        items(recipes) { recipe ->
          RecipeCard(
            recipe = recipe.recipe,
            onClick = { onRecipeSelected(recipe.recipe.id) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
  value: String, onValueChange: (String) -> Unit, onBack: () -> Unit
) {
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(true) {
    focusRequester.requestFocus()
  }

  TopAppBar(
    title = { }, colors = TopAppBarDefaults.topAppBarColors(
    actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
    navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = MaterialTheme.colorScheme.primaryContainer,
  ), actions = {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
      SearchTextField(
        value = value,
        placeholder = stringResource(R.string.textfield_placeholder_search),
        onValueChange = onValueChange,
        modifier = Modifier.focusRequester(focusRequester)
      )
    }
  }, navigationIcon = {
    IconButton(onClick = { onBack() }) {
      Icon(
        painter = painterResource(R.drawable.rounded_arrow_back_24),
        contentDescription = stringResource(R.string.cd_navigate_back)
      )
    }
  })
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeSearchScreenContent(
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
    ), searchWord = String.EMPTY, onRecipeSelected = {}, onBack = {}, onSearchWordChange = {})
  }
}