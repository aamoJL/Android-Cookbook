package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class RecipeScreenViewModel(
  private val recipeRepository: RecipeRepository
) : ViewModel() {

  /**
   * Initializer for this viewmodel used in [ViewModelProvider.Factory]
   */
  fun init(recipeId: Int) {
    viewModelScope.launch {
      launch {
        val recipeWithFavoriteAndRating =
          recipeRepository.getRecipeWithFavoriteAndRating(recipeId)
        _favoriteState.update {
          recipeWithFavoriteAndRating?.favorite != null
        }
        _completedPageUiState.update { s ->
          s.copy(fiveStarRating = recipeWithFavoriteAndRating?.rating?.ratingOutOfFive ?: 0)
        }
      }
      launch {
        recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
          ?: RecipeWithChaptersStepsAndIngredients(Recipe())
        _chapterPageUiStates.update {
          recipe.chapters.map { chapter ->
            ChapterPageUiState.fromChapter(chapter)
          }
        }
        _summaryPageUiStates.update {
          SummaryPageUiState(
            recipeName = recipe.value.name,
            chaptersWithIngredients = recipe.chapters.map { chapter ->
              Pair(
                chapter.value.name,
                chapter.steps.flatMap { it.ingredients }
              )
            }
          )
        }
        _servingsState.update {
          ServingsState(baseline = recipe.value.servings, current = recipe.value.servings)
        }
      }
    }
  }

  data class SummaryPageUiState(
    val recipeName: String = "",
    val chaptersWithIngredients: List<Pair<String, List<Ingredient>>> = emptyList()
  )

  data class ChapterPageUiState(
    val chapter: ChapterWithStepsAndIngredients,
    val progress: List<Boolean>
  ) {
    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients): ChapterPageUiState {
        return ChapterPageUiState(
          chapter = chapter,
          progress = chapter.steps.map { false }
        )
      }
    }
  }

  data class CompletedPageUiState(
    val fiveStarRating: Int = 0,
  )

  data class ServingsState(
    val baseline: Int = 1,
    val current: Int = 1,
  ) {
    val multiplier: Float = current.toFloat() / max(1, baseline).toFloat()
  }

  var recipe: RecipeWithChaptersStepsAndIngredients =
    RecipeWithChaptersStepsAndIngredients(Recipe())

  private val _summaryPageUiStates = MutableStateFlow(SummaryPageUiState())
  val summaryPageUiStates = _summaryPageUiStates.asStateFlow()

  private val _chapterPageUiStates = MutableStateFlow<List<ChapterPageUiState>>(emptyList())
  val chapterPageUiStates = _chapterPageUiStates.asStateFlow()

  private val _completedPageUiState = MutableStateFlow(CompletedPageUiState())
  val completedPageUiStates = _completedPageUiState.asStateFlow()

  private val _servingsState = MutableStateFlow(ServingsState())
  val servingsState = _servingsState.asStateFlow()

  private val _favoriteState = MutableStateFlow(false)
  val favoriteState = _favoriteState.asStateFlow()

  fun updateProgress(chapterIndex: Int, stepIndex: Int, value: Boolean) {
    _chapterPageUiStates.update { list ->
      list.mapIndexed { stateIndex, state ->
        if (stateIndex == chapterIndex) state.copy(
          progress = state.progress.toMutableList().apply {
            this[stepIndex] = value
          }
        )
        else state
      }
    }
  }

  fun setServingsCount(count: Int) =
    _servingsState.update { it.copy(current = max(1, count)) }

  fun setFavoriteState(value: Boolean) {
    viewModelScope.launch {
      if (value) recipeRepository.addRecipeToFavorites(recipe.value.id)
      else recipeRepository.removeRecipeFromFavorites(recipe.value.id)

      _favoriteState.update { value }
    }
  }

  fun setRating(value: Int) {
    if (value != _completedPageUiState.value.fiveStarRating) {
      viewModelScope.launch {
        recipeRepository.upsertRecipeRating(recipe.value.id, value)
        _completedPageUiState.update { s -> s.copy(fiveStarRating = value) }
      }
    }
    else {
      viewModelScope.launch {
        recipeRepository.deleteRecipeRating(recipe.value.id)
        _completedPageUiState.update { s -> s.copy(fiveStarRating = 0) }
      }
    }
  }
}