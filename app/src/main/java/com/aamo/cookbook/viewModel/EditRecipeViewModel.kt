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
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

  data class InfoScreenUiState(
    val id: Int = 0,
    val formState: InfoFormState = InfoFormState(),
    val chapters: List<ChapterWithStepsAndIngredients> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    data class InfoFormState(
      val name: String = "",
      val category: String = "",
      val subCategory: String = "",
      val servings: Int? = 1,
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
        servings = formState.servings ?: 1
      )
    }

    fun toRecipeWithChaptersStepsAndIngredients(): RecipeWithChaptersStepsAndIngredients =
      RecipeWithChaptersStepsAndIngredients(toRecipe(), chapters)

    companion object {
      fun fromRecipe(recipe: RecipeWithChaptersStepsAndIngredients): InfoScreenUiState {
        return InfoScreenUiState(
          id = recipe.value.id,
          formState = InfoFormState(
            name = recipe.value.name,
            category = recipe.value.category,
            subCategory = recipe.value.subCategory,
            servings = recipe.value.servings,
          ),
          chapters = recipe.chapters
        )
      }
    }
  }

  data class ChapterScreenUiState(
    val id: Int = 0,
    val formState: ChapterFormState = ChapterFormState(),
    val orderNumber: Int = 1,
    val steps: List<StepWithIngredients> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    data class ChapterFormState(
      val name: String = "",
    )

    val canBeSaved: Boolean
      get() = formState.name.isNotEmpty() && steps.isNotEmpty()

    private fun toChapter(): Chapter = Chapter(id, orderNumber, formState.name)
    fun toChapterWithStepsAndIngredients(): ChapterWithStepsAndIngredients =
      ChapterWithStepsAndIngredients(toChapter(), steps)

    companion object {
      fun fromChapter(chapter: ChapterWithStepsAndIngredients): ChapterScreenUiState {
        return ChapterScreenUiState(
          id = chapter.value.id,
          formState = ChapterFormState(
            name = chapter.value.name
          ),
          orderNumber = chapter.value.orderNumber,
          steps = chapter.steps
        )
      }
    }
  }

  data class StepScreenUiState(
    val id: Int = 0,
    val formState: StepFormState = StepFormState(),
    val orderNumber: Int = 1,
    val ingredients: List<Ingredient> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    data class StepFormState(
      val description: String = "",
    )

    val canBeSaved : Boolean
      get() = formState.description.isNotEmpty()

    private fun toStep(): Step = Step(id, orderNumber, formState.description)
    fun toStepWithIngredients(): StepWithIngredients = StepWithIngredients(toStep(), ingredients)

    companion object {
      fun fromStep(step: StepWithIngredients): StepScreenUiState {
        return StepScreenUiState(
          id = step.value.id,
          formState = StepFormState(
            description = step.value.description,
          ),
          orderNumber = step.value.orderNumber,
          ingredients = step.ingredients,
        )
      }
    }
  }

  data class IngredientScreenUiState(
    val id: Int = 0,
    val index: Int = -1,
    val formState: IngredientFormState = IngredientFormState(),
    val unsavedChanges: Boolean = false
  ) {
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
          ),
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

  private fun initInfoUiState(recipe: RecipeWithChaptersStepsAndIngredients) =
    _infoUiState.update { InfoScreenUiState.fromRecipe(recipe) }

  fun initChapterUiState(chapter: ChapterWithStepsAndIngredients) =
    _chapterUiState.update { ChapterScreenUiState.fromChapter(chapter) }

  fun initStepUiState(step: StepWithIngredients) =
    _stepUiState.update { StepScreenUiState.fromStep(step) }

  fun initIngredientUiState(ingredient: Ingredient, index: Int) =
    _ingredientUiState.update { IngredientScreenUiState.fromIngredient(ingredient, index) }

  fun addOrUpdateChapter(chapter: ChapterWithStepsAndIngredients) {
    val index = chapter.value.orderNumber - 1
    val chapters = _infoUiState.value.chapters.toMutableList()

    if (chapters.elementAtOrNull(index) != null) {
      chapters[index] = chapter
    } else {
      chapters.add(chapter)
    }

    _infoUiState.update { s ->
      s.copy(
        chapters = chapters,
        unsavedChanges = true,
      )
    }
  }

  fun addOrUpdateStep(step: StepWithIngredients) {
    val index = step.value.orderNumber - 1
    val steps = _chapterUiState.value.steps.toMutableList()

    if (steps.elementAtOrNull(index) != null) {
      steps[index] = step
    } else {
      steps.add(step)
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
      ingredients[index] = ingredient
    } else {
      ingredients.add(ingredient)
    }

    _stepUiState.update { s ->
      s.copy(
        ingredients = ingredients,
        unsavedChanges = true
      )
    }
  }

  fun setInfoFormState(state: InfoScreenUiState.InfoFormState) =
    _infoUiState.update {
      it.copy(
        formState = state,
        unsavedChanges = true
      )
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
}