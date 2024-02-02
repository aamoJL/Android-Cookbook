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
    val categorySuggestions: List<RecipeCategoryTuple> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    val isNewRecipe: Boolean = id == 0

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
    val unsavedChanges: Boolean = false,
  ) {
    val isNewChapter: Boolean = formState.name.isEmpty()

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
          steps = chapter.steps,
        )
      }
    }
  }

  data class StepScreenUiState(
    val id: Int = 0,
    val formState: StepFormState = StepFormState(),
    val orderNumber: Int = 1,
    val ingredients: List<Ingredient> = emptyList(),
    val unsavedChanges: Boolean = false,
  ) {
    val isNewStep: Boolean = formState.description.isEmpty()

    data class StepFormState(
      val description: String = "",
      val timerMinutes: Int? = null
    )

    val canBeSaved : Boolean
      get() = formState.description.isNotEmpty()

    private fun toStep(): Step = Step(
      id = id,
      orderNumber = orderNumber,
      description = formState.description,
      timerMinutes = if(formState.timerMinutes == 0) null else formState.timerMinutes)
    fun toStepWithIngredients(): StepWithIngredients = StepWithIngredients(toStep(), ingredients)

    companion object {
      fun fromStep(step: StepWithIngredients): StepScreenUiState {
        return StepScreenUiState(
          id = step.value.id,
          formState = StepFormState(
            description = step.value.description,
            timerMinutes = step.value.timerMinutes
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
    val index = chapters.indexOf(chapters.firstOrNull { it.value.orderNumber == chapter.value.orderNumber })

    if (chapters.elementAtOrNull(index) != null) {
      chapters[index] = chapter
    } else {
      chapters.add(chapter.copy(value = chapter.value.copy(orderNumber = chapters.size + 1)))
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
    val index = steps.indexOf(steps.firstOrNull { it.value.orderNumber == step.value.orderNumber })

    if (steps.elementAtOrNull(index) != null) {
      steps[index] = step
    } else {
      steps.add(step.copy(value = step.value.copy(orderNumber = steps.size + 1)))
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
    val index = _infoUiState.value.chapters.indexOf(chapter)

    if(index != -1) {
      _infoUiState.update { state ->
        state.copy(
          chapters = state.chapters.minus(chapter).let { list ->
            list.mapIndexed { index, chapter ->
              chapter.copy(value = chapter.value.copy(orderNumber = index + 1))
            }
          },
          unsavedChanges = true
        )
      }
    }

    return index != -1
  }

  fun deleteStep(step: StepWithIngredients) : Boolean {
    val index = _chapterUiState.value.steps.indexOf(step)

    if(index != -1) {
      _chapterUiState.update { state ->
        state.copy(
          steps = state.steps.minus(step).let { list ->
            list.mapIndexed { index, step ->
              step.copy(value = step.value.copy(orderNumber = index + 1))
            }
          },
          unsavedChanges = true
        )
      }
    }

    return index != -1
  }

  fun deleteIngredient(ingredient: Ingredient): Boolean {
    val index = _stepUiState.value.ingredients.indexOf(ingredient)

    if(index != -1) {
      _stepUiState.update { state ->
        state.copy(
          ingredients = state.ingredients.minus(ingredient),
          unsavedChanges = true
        )
      }
    }

    return index != -1
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