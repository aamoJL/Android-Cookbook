package com.aamo.cookbook.features.recipe.list.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.components.inputs.BackNavigationIconButton
import com.aamo.cookbook.ui.components.inputs.text_field.SearchTextField
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.extensions.general.EMPTY
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

@Serializable
data object RecipeSearchScreen

class RecipeSearchScreenViewModel(
  fetchData: () -> Flow<List<RecipeListRecipeModel>>,
) : ViewModel() {
  private var _nameFilter = MutableStateFlow(String.EMPTY)
  val nameFilter = _nameFilter.asStateFlow()

  val recipes = combine(fetchData(), _nameFilter) { recipe, word ->
    recipe.filter { it.recipe.name.contains(word, ignoreCase = true) }
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000L), initialValue = null
  )

  fun updateFilter(value: String) {
    _nameFilter.update { value }
  }
}

fun NavGraphBuilder.recipeSearchScreen(
  onOpenRecipe: (id: Long) -> Unit, onBack: () -> Unit
) {
  composable<RecipeSearchScreen> {
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipeSearchScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeSearchScreenViewModel(fetchData = { fetchRecipes(dao) })
      }
    })
    val recipes by viewmodel.recipes.collectAsStateWithLifecycle()
    val nameFilter by viewmodel.nameFilter.collectAsStateWithLifecycle()

    LoadingScreen(loading = recipes == null) {
      RecipeSearchScreenContent(
        recipes = checkNotNull(recipes),
        searchWord = nameFilter,
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
      SearchTopBar(
        value = searchWord,
        onValueChange = onSearchWordChange,
        onBack = onBack,
      )
    }) {
    BackgroundSurface(
      Modifier
        .padding(it)
        .fillMaxSize()
    ) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp)
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
              .padding(4.dp) // Prevents shadow clipping
              .testTag(UITag.OPTION.name)
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
  value: String, onValueChange: (String) -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier,
) {
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(true) {
    focusRequester.requestFocus()
  }

  TopAppBar(
    title = { }, colors = TopAppBarDefaults.topAppBarColors(
    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
    containerColor = MaterialTheme.colorScheme.primary,
  ), actions = {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
      SearchTextField(
        value = value,
        placeholder = stringResource(R.string.ph_search),
        onValueChange = onValueChange,
        modifier = Modifier.focusRequester(focusRequester)
      )
    }
  }, navigationIcon = {
    BackNavigationIconButton(onBack = onBack)
  }, modifier = modifier
  )
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeSearchScreenContent(
      recipes = listOf(
      RecipeListRecipeModel(recipe = Recipe(name = "Recipe 1"), rating = 3, isBookmarked = false),
      RecipeListRecipeModel(
        recipe = Recipe(name = "Recipe 1"), rating = null, isBookmarked = false
      ),
      RecipeListRecipeModel(
        recipe = Recipe(name = "Recipe 1"), rating = 5, isBookmarked = true
      ),
    ), searchWord = String.EMPTY, onRecipeSelected = {}, onBack = {}, onSearchWordChange = {})
  }
}