package com.aamo.cookbook.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.Screen
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeScreenViewModel(
  private val recipeRepository: RecipeRepository,
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  fun init() {
    viewModelScope.launch {
      val recipeId = savedStateHandle[Screen.Recipe.argumentName] ?: 0
      recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
        ?: RecipeWithChaptersStepsAndIngredients(Recipe())
    }.invokeOnCompletion {
      _currentProgress.update {
        recipe.chapters.map { x -> x.steps.map { false } }
      }
    }
  }

  var recipe: RecipeWithChaptersStepsAndIngredients = RecipeWithChaptersStepsAndIngredients(Recipe())

  private val _currentProgress: MutableStateFlow<List<List<Boolean>>> =
    MutableStateFlow(listOf(emptyList()))

  val currentProgress = _currentProgress.asStateFlow()

  fun updateProgress(chapterIndex: Int, stepIndex: Int, value: Boolean) {
    _currentProgress.update {
      it.mapIndexed { ci, chapters ->
        chapters.mapIndexed { si, stepValue ->
          if (ci == chapterIndex && si == stepIndex) value
          else stepValue
        }
      }
    }
  }
}