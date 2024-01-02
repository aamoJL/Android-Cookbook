package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class RecipeScreenViewModel : ViewModel() {
  private var _recipe = MutableStateFlow(Recipe("", ""))
  val recipe = _recipe.asStateFlow()

  private var _currentProgress = MutableStateFlow<List<Int>>(emptyList())
  val currentProgress = _currentProgress.asStateFlow()

  val currentChapter = _currentProgress.map {
    it.withIndex().indexOfFirst { item ->
      item.value != recipe.value.chapters.elementAtOrNull(item.index)?.steps?.size
    }
  }

  fun getRecipe(id: UUID) {
    viewModelScope.launch {
      val repo = AppViewModel.Repositories.recipeRepository
      val fetchedRecipe = repo.getRecipe(id)
      if (fetchedRecipe != null) {
        _recipe.value = fetchedRecipe
        _currentProgress.update { fetchedRecipe.chapters.map { 0 } }
      }
    }
  }

  fun updateProgress(index: Int, value: Int) {
    _currentProgress.update {
      it.mapIndexed { i, item ->
        if (i == index) value else item
      }
    }
  }
}