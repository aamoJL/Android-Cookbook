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
  fun init() {
    viewModelScope.launch {
      val recipeId = savedStateHandle[Screen.Recipe.argumentName] ?: 0
      val recipe = recipeRepository.getRecipeWithChaptersStepsAndIngredients(recipeId)
      initInfoUiState(recipe?.value, recipe?.chapters)
    }
  }

  data class InfoScreenUiState(
    val id: Int = 0,
    val name: String = "",
    val category: String = "",
    val subCategory: String = "",
    val servings: Int? = 1,
    val chapters: List<ChapterWithStepsAndIngredients> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    fun toRecipe(): Recipe {
      return Recipe(
        id = id,
        name = name,
        category = category,
        subCategory = subCategory,
        servings = servings ?: 1
      )
    }
    fun toRecipeWithChaptersStepsAndIngredients(): RecipeWithChaptersStepsAndIngredients =
      RecipeWithChaptersStepsAndIngredients(toRecipe(), chapters)
  }

  data class ChapterScreenUiState(
    val id: Int = 0,
    val name: String = "",
    val orderNumber: Int = 1,
    val steps: List<StepWithIngredients> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    private fun toChapter(): Chapter = Chapter(id, orderNumber, name)
    fun toChapterWithStepsAndIngredients(): ChapterWithStepsAndIngredients =
      ChapterWithStepsAndIngredients(toChapter(), steps)
  }

  data class StepScreenUiState(
    val id: Int = 0,
    val description: String = "",
    val orderNumber: Int = 1,
    val ingredients: List<Ingredient> = emptyList(),
    val unsavedChanges: Boolean = false
  ) {
    private fun toStep(): Step = Step(id, orderNumber, description)
    fun toStepWithIngredients(): StepWithIngredients = StepWithIngredients(toStep(), ingredients)
  }

  data class IngredientScreenUiState(
    val index: Int = -1,
    val id: Int = 0,
    val name: String = "",
    val amount: Float? = null,
    val unit: String = "",
    val unsavedChanges: Boolean = false
  ) {
    fun toIngredient(): Ingredient = Ingredient(id, name, amount ?: 0f, unit)
  }

  private val _infoUiState = MutableStateFlow(InfoScreenUiState())
  val infoUiState: StateFlow<InfoScreenUiState> = _infoUiState.asStateFlow()

  private val _chapterUiState = MutableStateFlow(ChapterScreenUiState())
  val chapterUiState: StateFlow<ChapterScreenUiState> = _chapterUiState.asStateFlow()

  private val _stepUiState = MutableStateFlow(StepScreenUiState())
  val stepUiState: StateFlow<StepScreenUiState> = _stepUiState.asStateFlow()

  private val _ingredientUiState = MutableStateFlow(IngredientScreenUiState())
  val ingredientUiState: StateFlow<IngredientScreenUiState> = _ingredientUiState.asStateFlow()

  private fun initInfoUiState(recipe: Recipe?, chapters: List<ChapterWithStepsAndIngredients>?) {
    val value = recipe ?: Recipe()

    _infoUiState.update { s ->
      s.copy(
        id = value.id,
        name = value.name,
        category = value.category,
        subCategory = value.subCategory,
        servings = value.servings,
        chapters = chapters ?: emptyList(),
        unsavedChanges = false
      )
    }
  }

  fun initChapterUiState(chapter: Chapter?, steps: List<StepWithIngredients>?) {
    val value = chapter ?: Chapter()

    _chapterUiState.update { s ->
      s.copy(
        id = value.id,
        name = value.name,
        orderNumber = value.orderNumber,
        steps = steps ?: emptyList(),
        unsavedChanges = false
      )
    }
  }

  fun initStepUiState(step: Step?, ingredients: List<Ingredient>?) {
    val value = step ?: Step()

    _stepUiState.update { s ->
      s.copy(
        id = value.id,
        description = value.description,
        orderNumber = value.orderNumber,
        ingredients = ingredients ?: emptyList(),
        unsavedChanges = false
      )
    }
  }

  fun initIngredientUiState(ingredient: Ingredient?, index: Int) {
    val value = ingredient ?: Ingredient()

    _ingredientUiState.update { s ->
      s.copy(
        index = index,
        id = value.id,
        name = value.name,
        amount = value.amount,
        unit = value.unit,
        unsavedChanges = false
      )
    }
  }

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
        unsavedChanges = true
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

  fun setRecipeName(value: String) =
    _infoUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setCategory(value: String) =
    _infoUiState.update { s -> s.copy(category = value, unsavedChanges = true) }

  fun setSubCategory(value: String) =
    _infoUiState.update { s -> s.copy(subCategory = value, unsavedChanges = true) }

  fun setServings(value: Int?) =
    _infoUiState.update { s -> s.copy(servings = value, unsavedChanges = true) }

  fun setChapterName(value: String) =
    _chapterUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setStepDescription(value: String) =
    _stepUiState.update { s -> s.copy(description = value, unsavedChanges = true) }

  fun setIngredientName(value: String) =
    _ingredientUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setIngredientUnit(value: String) =
    _ingredientUiState.update { s -> s.copy(unit = value, unsavedChanges = true) }

  fun setIngredientAmount(value: Float?) =
    _ingredientUiState.update { s -> s.copy(amount = value, unsavedChanges = true) }
}