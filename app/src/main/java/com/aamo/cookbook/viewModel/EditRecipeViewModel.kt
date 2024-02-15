package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.database.repository.RecipeRepository
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class EditRecipeViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

  /**
   * Initializer for this viewmodel used in [ViewModelProvider.Factory]
   */
  fun init(recipeId: Int) {
    viewModelScope.launch {
      val recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
        ?: RecipeWithChaptersStepsAndIngredients(Recipe())
      _recipeInfo = RecipeInfo(id = recipe.value.id, thumbnailUri = recipe.value.thumbnailUri)
      initInfoUiState(recipe)
    }
  }

  /**
   * Recipe's information that can't be changed from the editRecipe screens
   */
  data class RecipeInfo(
    val id: Int,
    val thumbnailUri: String
  )

  /**
   * @param chapters Pair of [UUID] and [ChapterWithStepsAndIngredients], the UUID is used as a list key
   */
  data class InfoScreenUiState(
    val formState: InfoFormState = InfoFormState(),
    val chapters: List<Pair<UUID, ChapterWithStepsAndIngredients>> = emptyList(),
    val categorySuggestions: List<RecipeCategoryTuple> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    val isNewRecipe: Boolean = formState.name.isEmpty()

    data class InfoFormState(
      val name: String = "",
      val category: String = "",
      val subCategory: String = "",
      val servings: Int? = 1,
      val note: String = ""
    )

    val canBeSaved: Boolean
      get() = formState.name.isNotEmpty()
              && formState.category.isNotEmpty()
              && chapters.isNotEmpty()

    private fun toRecipe() = Recipe(
      name = formState.name,
      category = formState.category,
      subCategory = formState.subCategory,
      servings = formState.servings ?: 1,
      note = formState.note,
    )

    fun toRecipeWithChaptersStepsAndIngredients() =
      RecipeWithChaptersStepsAndIngredients(toRecipe(), chapters.map { it.second })

    companion object {
      fun fromRecipe(recipe: RecipeWithChaptersStepsAndIngredients): InfoScreenUiState {
        return InfoScreenUiState(
          formState = InfoFormState(
            name = recipe.value.name,
            category = recipe.value.category,
            subCategory = recipe.value.subCategory,
            servings = recipe.value.servings,
            note = recipe.value.note
          ),
          chapters = recipe.chapters.map { Pair(UUID.randomUUID(), it) }
        )
      }
    }
  }

  /**
   * @param steps Pair of [UUID] and [StepWithIngredients], the UUID is used as a list key
   */
  data class ChapterScreenUiState(
    val index: Int = -1,
    val formState: ChapterFormState = ChapterFormState(),
    val steps: List<Pair<UUID, StepWithIngredients>> = emptyList(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewChapter: Boolean = formState.name.isEmpty()

    data class ChapterFormState(
      val name: String = "",
      val note: String = ""
    )

    val canBeSaved: Boolean
      get() = formState.name.isNotEmpty() && steps.isNotEmpty()

    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients, index: Int): ChapterScreenUiState {
        return ChapterScreenUiState(
          index = index,
          formState = ChapterFormState(
            name = chapter.value.name,
            note = chapter.value.note
          ),
          steps = chapter.steps.map { Pair(UUID.randomUUID(), it) },
        )
      }
    }
  }

  /**
   * @param ingredients Pair of [UUID] and [Ingredient], the UUID is used as a list key
   */
  data class StepScreenUiState(
    val index: Int = -1,
    val formState: StepFormState = StepFormState(),
    val ingredients: List<Pair<UUID, Ingredient>> = emptyList(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewStep: Boolean = formState.description.isEmpty()

    data class StepFormState(
      val description: String = "",
      val timerMinutes: Int? = null,
      val note: String = ""
    )

    val canBeSaved: Boolean
      get() = formState.description.isNotEmpty()

    companion object {
      fun fromStep(step: StepWithIngredients, index: Int): StepScreenUiState {
        return StepScreenUiState(
          index = index,
          formState = StepFormState(
            description = step.value.description,
            timerMinutes = step.value.timerMinutes,
            note = step.value.note
          ),
          ingredients = step.ingredients.map { Pair(UUID.randomUUID(), it) },
        )
      }
    }
  }

  data class IngredientScreenUiState(
    val index: Int = -1,
    val formState: IngredientFormState = IngredientFormState(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewIngredient: Boolean = formState.name.isEmpty()

    data class IngredientFormState(
      val name: String = "",
      val amount: Float? = 0f,
      val unit: String = "",
    )

    val canBeSaved: Boolean
      get() = formState.name.isNotEmpty()

    companion object {
      fun fromIngredient(ingredient: Ingredient, index: Int): IngredientScreenUiState {
        return IngredientScreenUiState(
          index = index,
          formState = IngredientFormState(
            name = ingredient.name,
            amount = ingredient.amount,
            unit = ingredient.unit
          )
        )
      }
    }
  }

  private lateinit var _recipeInfo: RecipeInfo

  private val _infoUiState = MutableStateFlow(InfoScreenUiState())
  var infoUiState: StateFlow<InfoScreenUiState> = _infoUiState.asStateFlow()

  private val _chapterUiState = MutableStateFlow(ChapterScreenUiState())
  val chapterUiState: StateFlow<ChapterScreenUiState> = _chapterUiState.asStateFlow()

  private val _stepUiState = MutableStateFlow(StepScreenUiState())
  val stepUiState: StateFlow<StepScreenUiState> = _stepUiState.asStateFlow()

  private val _ingredientUiState = MutableStateFlow(IngredientScreenUiState())
  val ingredientUiState: StateFlow<IngredientScreenUiState> = _ingredientUiState.asStateFlow()

  private fun initInfoUiState(recipe: RecipeWithChaptersStepsAndIngredients) {
    viewModelScope.launch {
      val categoriesWithSubcategories = recipeRepository.getCategoriesWithSubCategories()

      _infoUiState.update {
        InfoScreenUiState.fromRecipe(recipe).copy(categorySuggestions = categoriesWithSubcategories)
      }
    }
  }

  fun initChapterUiState(index: Int) =
    _infoUiState.value.chapters.getOrNull(index).also { chapterPair ->
      _chapterUiState.update {
        ChapterScreenUiState.fromChapter(
          chapter = chapterPair?.second
            ?: ChapterWithStepsAndIngredients(Chapter()),
          index = chapterPair?.let { index } ?: -1
        )
      }
    }

  fun initStepUiState(index: Int) =
    _chapterUiState.value.steps.getOrNull(index).also { stepPair ->
      _stepUiState.update {
        StepScreenUiState.fromStep(
          step = stepPair?.second ?: StepWithIngredients(Step()),
          index = stepPair?.let { index } ?: -1
        )
      }
    }

  fun initIngredientUiState(index: Int) =
    _stepUiState.value.ingredients.getOrNull(index).also { ingredientPair ->
      _ingredientUiState.update {
        IngredientScreenUiState.fromIngredient(
          ingredient = ingredientPair?.second ?: Ingredient(),
          index = ingredientPair?.let { index } ?: -1
        )
      }
    }

  fun applyChapterChanges() {
    _infoUiState.update { s ->
      s.copy(
        chapters = _infoUiState.value.chapters.toMutableList().apply {
          this.elementAtOrNull(_chapterUiState.value.index)?.also { existing ->
            this[_chapterUiState.value.index] = existing.copy(
              second = existing.second.copy(
                value = existing.second.value.copy(
                  name = _chapterUiState.value.formState.name,
                  note = _chapterUiState.value.formState.note
                ),
                steps = _chapterUiState.value.steps.map { it.second }
              )
            )
          } ?: this.add(
            Pair(
              UUID.randomUUID(), ChapterWithStepsAndIngredients(
                value = Chapter(
                  name = _chapterUiState.value.formState.name,
                  note = _chapterUiState.value.formState.note
                ),
                steps = _chapterUiState.value.steps.map { it.second }
              )
            )
          )
        },
        unsavedChanges = true,
      )
    }
  }

  fun applyStepChanges() {
    _chapterUiState.update { s ->
      s.copy(
        steps = _chapterUiState.value.steps.toMutableList().apply {
          this.elementAtOrNull(_stepUiState.value.index)?.also { existing ->
            this[_stepUiState.value.index] = existing.copy(
              second = existing.second.copy(
                value = existing.second.value.copy(
                  description = _stepUiState.value.formState.description,
                  timerMinutes = _stepUiState.value.formState.timerMinutes,
                  note = _stepUiState.value.formState.note
                ),
                ingredients = _stepUiState.value.ingredients.map { it.second }
              )
            )
          } ?: this.add(
            Pair(
              UUID.randomUUID(), StepWithIngredients(
                value = Step(
                  description = _stepUiState.value.formState.description,
                  timerMinutes = _stepUiState.value.formState.timerMinutes,
                  note = _stepUiState.value.formState.note
                ),
                ingredients = _stepUiState.value.ingredients.map { it.second }
              )
            )
          )
        },
        unsavedChanges = true
      )
    }
  }

  fun applyIngredientChanges() {
    _stepUiState.update { s ->
      s.copy(
        ingredients = _stepUiState.value.ingredients.toMutableList().apply {
          this.elementAtOrNull(_ingredientUiState.value.index)?.also { existing ->
            this[_ingredientUiState.value.index] = existing.copy(
              second = existing.second.copy(
                name = _ingredientUiState.value.formState.name,
                amount = _ingredientUiState.value.formState.amount ?: 1f,
                unit = _ingredientUiState.value.formState.unit
              )
            )
          } ?: this.add(
            Pair(
              UUID.randomUUID(), Ingredient(
                name = _ingredientUiState.value.formState.name,
                amount = _ingredientUiState.value.formState.amount ?: 1f,
                unit = _ingredientUiState.value.formState.unit
              )
            )
          )
        },
        unsavedChanges = true
      )
    }
  }

  fun setInfoFormState(state: InfoScreenUiState.InfoFormState) {
    _infoUiState.update {
      it.copy(
        formState = state,
        unsavedChanges = true
      )
    }
  }

  fun setChapterFormState(state: ChapterScreenUiState.ChapterFormState) =
    _chapterUiState.update {
      it.copy(
        formState = state,
        unsavedChanges = true
      )
    }

  fun setStepFormState(state: StepScreenUiState.StepFormState) {
    _stepUiState.update {
      it.copy(
        formState = state,
        unsavedChanges = true
      )
    }
  }

  fun setIngredientFormState(state: IngredientScreenUiState.IngredientFormState) {
    _ingredientUiState.update {
      it.copy(
        formState = state,
        unsavedChanges = true
      )
    }
  }

  fun deleteChapter(index: Int): Boolean {
    return _infoUiState.value.chapters.getOrNull(index)?.also { existing ->
        _infoUiState.update { state ->
          state.copy(
            chapters = state.chapters.minus(existing),
            unsavedChanges = true
          )
        }
      } != null
  }

  fun deleteStep(index: Int): Boolean {
    return _chapterUiState.value.steps.getOrNull(index)?.also { existing ->
        _chapterUiState.update { state ->
          state.copy(
            steps = state.steps.minus(existing),
            unsavedChanges = true
          )
        }
      } != null
  }

  fun deleteIngredient(index: Int): Boolean {
    return _stepUiState.value.ingredients.getOrNull(index)
      ?.also { existing ->
        _stepUiState.update { state ->
          state.copy(
            ingredients = state.ingredients.minus(existing),
            unsavedChanges = true
          )
        }
      } != null
  }

  fun swapChapterPositions(from: Int, to: Int) {
    _infoUiState.update { s ->
      s.copy(
        chapters = _infoUiState.value.chapters.toMutableList().apply {
          if (from in 0..this.size && to in 0..this.size) {
            this[to] = this[from].also { this[from] = this[to] }
          }
        },
        unsavedChanges = true
      )
    }
  }

  fun swapStepPositions(from: Int, to: Int) {
    _chapterUiState.update { s ->
      s.copy(
        steps = _chapterUiState.value.steps.toMutableList().apply {
          if (from in 0..this.size && to in 0..this.size) {
            this[to] = this[from].also { this[from] = this[to] }
          }
        },
        unsavedChanges = true
      )
    }
  }

  fun swapIngredientPositions(from: Int, to: Int) {
    _stepUiState.update { s ->
      s.copy(
        ingredients = _stepUiState.value.ingredients.toMutableList().apply {
          if (from in 0..this.size && to in 0..this.size) {
            this[to] = this[from].also { this[from] = this[to] }
          }
        },
        unsavedChanges = true
      )
    }
  }

  fun toRecipeWithChaptersStepsAndIngredients() =
    _infoUiState.value.toRecipeWithChaptersStepsAndIngredients().let {
      it.copy(value = it.value.copy(id = _recipeInfo.id, thumbnailUri = _recipeInfo.thumbnailUri))
    }
}