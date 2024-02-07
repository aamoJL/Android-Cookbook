package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.database.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RecipeSearchViewModel(recipeRepository: RecipeRepository) : ViewModel() {
  private var _recipesStream = recipeRepository.getRecipesWithFavoriteAndRatingFlow()

  private var _searchWord = MutableStateFlow("")
  val searchWord = _searchWord.asStateFlow()

  val validRecipes = combine(_recipesStream, _searchWord) { recipes, word ->
    recipes.filter { it.value.name.startsWith(word, true) }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = emptyList()
  )

  fun setSearchWord(value: String) {
    _searchWord.update { value }
  }
}