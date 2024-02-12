package com.aamo.cookbook.viewModel

import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.service.IOServiceBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class RecipeScreenViewModel(
  private val recipeRepository: RecipeRepository,
  private val ioService: IOServiceBase
) : ViewModel() {

  /**
   * Initializer for this viewmodel used in [ViewModelProvider.Factory]
   */
  fun init(recipeId: Int) {
    this.recipeId = recipeId

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
        val recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
          ?: RecipeWithChaptersStepsAndIngredients(Recipe())
        _chapterPageUiStates.update {
          recipe.chapters.map { chapter ->
            ChapterPageUiState.fromChapter(chapter)
          }
        }
        _summaryPageUiStates.update {
          SummaryPageUiState(
            recipeName = recipe.value.name,
            recipeNote = recipe.value.note,
            recipeThumbnail = recipe.value.thumbnailUri,
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
        _completedPageUiState.update { s ->
          s.copy(thumbnailFileName = recipe.value.thumbnailUri)
        }
      }
    }
  }

  data class SummaryPageUiState(
    val recipeName: String = "",
    val recipeNote: String = "",
    val recipeThumbnail: String = "",
    val chaptersWithIngredients: List<Pair<String, List<Ingredient>>> = emptyList()
  )

  data class ChapterPageUiState(
    val chapter: ChapterWithStepsAndIngredients,
    val progress: List<Boolean>,
    val chapterNote: String = "",
  ) {
    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients): ChapterPageUiState {
        return ChapterPageUiState(
          chapter = chapter,
          progress = chapter.steps.map { false },
        )
      }
    }
  }

  data class CompletedPageUiState(
    val fiveStarRating: Int = 0,
    val thumbnailFileName: String = ""
  )

  data class ServingsState(
    val baseline: Int = 1,
    val current: Int = 1,
  ) {
    val multiplier: Float = current.toFloat() / max(1, baseline).toFloat()
  }

  var recipeId: Int = 0
    private set

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
      if (value) recipeRepository.addRecipeToFavorites(recipeId)
      else recipeRepository.removeRecipeFromFavorites(recipeId)

      _favoriteState.update { value }
    }
  }

  fun setRating(value: Int) {
    if (value != _completedPageUiState.value.fiveStarRating) {
      viewModelScope.launch {
        recipeRepository.upsertRecipeRating(recipeId, value)
        _completedPageUiState.update { s -> s.copy(fiveStarRating = value) }
      }
    } else {
      viewModelScope.launch {
        recipeRepository.deleteRecipeRating(recipeId)
        _completedPageUiState.update { s -> s.copy(fiveStarRating = 0) }
      }
    }
  }

  fun setThumbnail(uri: Uri) {
    viewModelScope.launch {
      val oldThumbnail = _completedPageUiState.value.thumbnailFileName
      if (oldThumbnail.isNotEmpty()) {
        ioService.deleteExternalFile(Environment.DIRECTORY_PICTURES, oldThumbnail)
      }

      recipeRepository.getRecipeById(recipeId)
        ?.copy(thumbnailUri = ioService.getFileNameWithSuffixFromUri(uri) ?: "")
        ?.also {
          recipeRepository.upsertRecipe(it)
          _completedPageUiState.update { s -> s.copy(thumbnailFileName = it.thumbnailUri) }
          _summaryPageUiStates.update { s -> s.copy(recipeThumbnail = it.thumbnailUri) }
        }
    }
  }
}