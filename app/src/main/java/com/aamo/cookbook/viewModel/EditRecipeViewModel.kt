package com.aamo.cookbook.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aamo.cookbook.Screen
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

class EditRecipeViewModel(private val recipeRepository: RecipeRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {

  /**
   * Initializer for this viewmodel used in [ViewModelProvider.Factory]
   */
  fun init() {
    viewModelScope.launch {
      val recipeId = savedStateHandle[Screen.Recipe.argumentName] ?: 0
      val recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
      initInfoUiState(recipe ?: RecipeWithChaptersStepsAndIngredients(Recipe()))
    }
  }

  /**
   * @param chapters Pair of [UUID] and [ChapterWithStepsAndIngredients], the UUID is used as a list key
   */
  data class InfoScreenUiState(
    val id: Int = 0,
    val formState: InfoFormState = InfoFormState(),
    val chapters: List<Pair<UUID, ChapterWithStepsAndIngredients>> = emptyList(),
    val categorySuggestions: List<RecipeCategoryTuple> = emptyList(),
    val thumbnailUri: String = "",
    val unsavedChanges: Boolean = false
  ) {
    val isNewRecipe: Boolean = id == 0

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

    fun toRecipe(): Recipe {
      return Recipe(
        id = id,
        name = formState.name,
        category = formState.category,
        subCategory = formState.subCategory,
        servings = formState.servings ?: 1,
        note = formState.note,
        thumbnailUri = thumbnailUri
      )
    }

    fun toRecipeWithChaptersStepsAndIngredients(): RecipeWithChaptersStepsAndIngredients =
      RecipeWithChaptersStepsAndIngredients(toRecipe(), chapters.map { it.second })

    companion object {
      fun fromRecipe(recipe: RecipeWithChaptersStepsAndIngredients): InfoScreenUiState {
        return InfoScreenUiState(
          id = recipe.value.id,
          formState = InfoFormState(
            name = recipe.value.name,
            category = recipe.value.category,
            subCategory = recipe.value.subCategory,
            servings = recipe.value.servings,
            note = recipe.value.note
          ),
          chapters = recipe.chapters.map { Pair(UUID.randomUUID(), it) },
          thumbnailUri = recipe.value.thumbnailUri
        )
      }
    }
  }

  /**
   * @param steps Pair of [UUID] and [StepWithIngredients], the UUID is used as a list key
   */
  data class ChapterScreenUiState(
    val id: Int = 0,
    val formState: ChapterFormState = ChapterFormState(),
    val orderNumber: Int = 1,
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

    private fun toChapter(): Chapter = Chapter(
      id = id,
      orderNumber = orderNumber,
      name = formState.name,
      note = formState.note
    )

    fun toChapterWithStepsAndIngredients(): ChapterWithStepsAndIngredients =
      ChapterWithStepsAndIngredients(toChapter(), steps.map { it.second })

    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients): ChapterScreenUiState {
        return ChapterScreenUiState(
          id = chapter.value.id,
          formState = ChapterFormState(
            name = chapter.value.name,
            note = chapter.value.note
          ),
          orderNumber = chapter.value.orderNumber,
          steps = chapter.steps.map { Pair(UUID.randomUUID(), it) },
        )
      }
    }
  }

  /**
   * @param ingredients Pair of [UUID] and [Ingredient], the UUID is used as a list key
   */
  data class StepScreenUiState(
    val id: Int = 0,
    val formState: StepFormState = StepFormState(),
    val orderNumber: Int = 1,
    val ingredients: List<Pair<UUID, Ingredient>> = emptyList(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewStep: Boolean = formState.description.isEmpty()

    data class StepFormState(
      val description: String = "",
      val timerMinutes: Int? = null,
      val note: String = ""
    )

    val canBeSaved : Boolean
      get() = formState.description.isNotEmpty()

    private fun toStep(): Step = Step(
      id = id,
      orderNumber = orderNumber,
      description = formState.description,
      timerMinutes = if(formState.timerMinutes == 0) null else formState.timerMinutes,
      note = formState.note
    )

    fun toStepWithIngredients(): StepWithIngredients =
      StepWithIngredients(toStep(), ingredients.map { it.second })

    companion object {
      fun fromStep(step: StepWithIngredients): StepScreenUiState {
        return StepScreenUiState(
          id = step.value.id,
          formState = StepFormState(
            description = step.value.description,
            timerMinutes = step.value.timerMinutes,
            note = step.value.note
          ),
          orderNumber = step.value.orderNumber,
          ingredients = step.ingredients.map { Pair(UUID.randomUUID(), it) },
        )
      }
    }
  }

  data class IngredientScreenUiState(
    val id: Int = 0,
    val index: Int = -1,
    val formState: IngredientFormState = IngredientFormState(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewIngredient: Boolean = formState.name.isEmpty()

    data class IngredientFormState(
      val name: String = "",
      val amount: Float? = null,
      val unit: String = "",
    )

    val canBeSaved: Boolean
      get() = formState.name.isNotEmpty()

    fun toIngredient(): Ingredient =
      Ingredient(id, formState.name, formState.amount ?: 0f, formState.unit)

    companion object {
      fun fromIngredient(ingredient: Ingredient, index: Int): IngredientScreenUiState {
        return IngredientScreenUiState(
          index = index,
          id = ingredient.id,
          formState = IngredientFormState(
            name = ingredient.name,
            amount = ingredient.amount,
            unit = ingredient.unit
          )
        )
      }
    }
  }

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

  fun initChapterUiState(chapter: ChapterWithStepsAndIngredients) =
    _chapterUiState.update { ChapterScreenUiState.fromChapter(chapter) }

  fun initStepUiState(step: StepWithIngredients) =
    _stepUiState.update { StepScreenUiState.fromStep(step) }

  fun initIngredientUiState(ingredient: Ingredient, index: Int) =
    _ingredientUiState.update { IngredientScreenUiState.fromIngredient(ingredient, index) }

  fun addOrUpdateChapter(chapter: ChapterWithStepsAndIngredients) {
    val chapters = _infoUiState.value.chapters.toMutableList()
    val index =
      chapters.indexOf(chapters.firstOrNull { it.second.value.orderNumber == chapter.value.orderNumber })

    if (chapters.elementAtOrNull(index) != null) {
      chapters[index] = chapters[index].copy(second = chapter)
    } else {
      chapters.add(
        Pair(
          UUID.randomUUID(),
          chapter.copy(value = chapter.value.copy(orderNumber = chapters.size + 1))
        )
      )
    }

    _infoUiState.update { s ->
      s.copy(
        chapters = chapters,
        unsavedChanges = true,
      )
    }
  }

  fun addOrUpdateStep(step: StepWithIngredients) {
    val steps = _chapterUiState.value.steps.toMutableList()
    val index =
      steps.indexOf(steps.firstOrNull { it.second.value.orderNumber == step.value.orderNumber })

    if (steps.elementAtOrNull(index) != null) {
      steps[index] = steps[index].copy(second = step)
    } else {
      steps.add(
        Pair(
          UUID.randomUUID(),
          step.copy(value = step.value.copy(orderNumber = steps.size + 1)))
        )
    }

    _chapterUiState.update { s ->
      s.copy(
        steps = steps,
        unsavedChanges = true
      )
    }
  }

  fun addOrUpdateIngredient(ingredient: Ingredient, index: Int) {
    val ingredients = _stepUiState.value.ingredients.toMutableList()

    if (ingredients.elementAtOrNull(index) != null) {
      ingredients[index] = ingredients[index].copy(second = ingredient)
    } else {
      ingredients.add(Pair(UUID.randomUUID(), ingredient))
    }

    _stepUiState.update { s ->
      s.copy(
        ingredients = ingredients,
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

  fun deleteChapter(chapter: ChapterWithStepsAndIngredients) : Boolean {
    return _infoUiState.value.chapters.firstOrNull { it.second == chapter }?.also { existing ->
      _infoUiState.update { state ->
        state.copy(
          chapters = state.chapters.minus(existing).let { list ->
            list.mapIndexed { index, pair ->
              pair.copy(second = pair.second.copy(value = chapter.value.copy(orderNumber = index + 1)))
            }
          },
          unsavedChanges = true
        )
      }
    } != null
  }

  fun deleteStep(step: StepWithIngredients) : Boolean {
    return _chapterUiState.value.steps.firstOrNull { it.second == step }?.also { existing ->
      _chapterUiState.update { state ->
        state.copy(
          steps = state.steps.minus(existing).let { list ->
            list.mapIndexed { index, pair ->
              pair.copy(second = pair.second.copy(value = step.value.copy(orderNumber = index + 1)))
            }
          },
          unsavedChanges = true
        )
      }
    } != null
  }

  fun deleteIngredient(ingredient: Ingredient): Boolean {
    return _stepUiState.value.ingredients.firstOrNull { it.second == ingredient }
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
    val chapters = _infoUiState.value.chapters.toMutableList()

    if(from in 0..chapters.size && to in 0..chapters.size) {
      chapters[to] = chapters[from].also { chapters[from] = chapters[to] }
    }

    _infoUiState.update { s -> s.copy(chapters = chapters, unsavedChanges = true) }
  }

  fun swapStepPositions(from: Int, to: Int) {
    val steps = _chapterUiState.value.steps.toMutableList()

    if(from in 0..steps.size && to in 0..steps.size) {
      steps[to] = steps[from].also { steps[from] = steps[to] }
    }

    _chapterUiState.update { s -> s.copy(steps = steps, unsavedChanges = true) }
  }

  fun swapIngredientPositions(from: Int, to: Int) {
    val ingredients = _stepUiState.value.ingredients.toMutableList()

    if(from in 0..ingredients.size && to in 0..ingredients.size) {
      ingredients[to] = ingredients[from].also { ingredients[from] = ingredients[to] }
    }

    _stepUiState.update { s -> s.copy(ingredients = ingredients, unsavedChanges = true) }
  }
}