package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aamo.cookbook.R
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RecipeSearchViewModel(recipeRepository: RecipeRepository) : ViewModel() {
  private var _recipesStream = recipeRepository.getAllRecipesStream()

  private var _searchWord = MutableStateFlow("")
  val searchWord = _searchWord.asStateFlow()

  val validRecipes = combine(_recipesStream, _searchWord) { recipes , word ->
      recipes.filter { it.name.startsWith(word) }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  fun setSearchWord(value: String) {
    _searchWord.update { value }
  }
}

@Composable
fun RecipeSearchScreen(
  viewModel: RecipeSearchViewModel = viewModel(factory = ViewModelProvider.Factory),
  onBack: () -> Unit = {},
) {
  val searchWord by viewModel.searchWord.collectAsState()
  val recipes by viewModel.validRecipes.collectAsState()

  Scaffold(
    topBar = {
      SearchTopBar(
        value = searchWord,
        onValueChange = {viewModel.setSearchWord(it)},
        onBack = onBack
      )
    }
  ) {
    Surface(Modifier.padding(it)) {
      LazyColumn {
        items(recipes) { recipe ->
           ListItem(
             headlineContent = { Text(text = recipe.name) }
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
      TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.description_search)
          )
        },
        trailingIcon = {
          if(value.isNotEmpty()){
            IconButton(onClick = { onValueChange("") }) {
              Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear"
              )
            }
          }
        },
        shape = RectangleShape,
        singleLine = true,
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
    },
  )
}