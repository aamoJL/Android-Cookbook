package com.aamo.cookbook.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.Screen
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
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

  /**
   * Initializer for this viewmodel used in [ViewModelProvider.Factory]
   */
  fun init() {
    viewModelScope.launch {
      val recipeId = savedStateHandle[Screen.Recipe.argumentName] ?: 0
      recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
        ?: RecipeWithChaptersStepsAndIngredients(Recipe())
    }.invokeOnCompletion {
      _chapterPageUiStates.update {
        recipe.chapters.map { chapter ->
          ChapterPageUiState.fromChapter(chapter)
        }
      }
      _summaryPageUiStates.update { SummaryPageUiState(recipe) }
    }
  }

  data class SummaryPageUiState(
    val recipe: RecipeWithChaptersStepsAndIngredients =
      RecipeWithChaptersStepsAndIngredients(Recipe())
  )

  data class ChapterPageUiState(
    val chapter: ChapterWithStepsAndIngredients,
    val progress: List<Boolean>
  ){
    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients) : ChapterPageUiState {
        return ChapterPageUiState(
          chapter = chapter,
          progress = chapter.steps.map { false }
        )
      }
    }
  }

  var recipe: RecipeWithChaptersStepsAndIngredients =
    RecipeWithChaptersStepsAndIngredients(Recipe())

  private val _summaryPageUiStates = MutableStateFlow(SummaryPageUiState())
  val summaryPageUiStates = _summaryPageUiStates.asStateFlow()

  private val _chapterPageUiStates = MutableStateFlow<List<ChapterPageUiState>>(emptyList())
  val chapterPageUiStates = _chapterPageUiStates.asStateFlow()

  fun updateProgress(chapterIndex: Int, stepIndex: Int, value: Boolean) {
    _chapterPageUiStates.update { list ->
      list.mapIndexed { stateIndex, state ->
        if(stateIndex == chapterIndex) state.copy(
          progress = state.progress.toMutableList().apply {
            this[stepIndex] = value
          }
        )
        else state
      }
    }
  }
}