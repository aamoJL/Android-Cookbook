package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModel
import com.aamo.cookbook.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class EditRecipeViewModel : ViewModel() {
  data class InfoScreenUiState(
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val category: String = "",
    val subCategory: String = "",
    val servings: Int? = null,
    val chapters: Set<Recipe.Chapter> = emptySet(),
    val unsavedChanges: Boolean = false
  ) {

    fun toRecipe(): Recipe {
      return Recipe(name, category, subCategory, servings ?: 1, chapters, id)
    }
  }

  data class ChapterScreenUiState(
    val index: Int = -1,
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val steps: Set<Recipe.Chapter.Step> = emptySet(),
    val unsavedChanges: Boolean = false
  ) {
    fun toChapter(): Recipe.Chapter {
      return Recipe.Chapter(name, steps)
    }
  }

  data class StepScreenUiState(
    val index: Int = -1,
    val id: UUID = UUID(0, 0),
    val description: String = "",
    val ingredients: Set<Recipe.Ingredient> = emptySet(),
    val unsavedChanges: Boolean = false
  ) {
    fun toStep(): Recipe.Chapter.Step {
      return Recipe.Chapter.Step(description, ingredients)
    }
  }

  data class IngredientScreenUiState(
    val index: Int = -1,
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val amount: Float? = null,
    val unit: String = "",
    val unsavedChanges: Boolean = false
  ) {
    fun toIngredient(): Recipe.Ingredient {
      return Recipe.Ingredient(name, amount ?: 0f, unit)
    }
  }

  private val _infoUiState = MutableStateFlow(InfoScreenUiState())
  val infoUiState: StateFlow<InfoScreenUiState> = _infoUiState.asStateFlow()

  private val _chapterUiState = MutableStateFlow(ChapterScreenUiState())
  val chapterUiState: StateFlow<ChapterScreenUiState> = _chapterUiState.asStateFlow()

  private val _stepUiState = MutableStateFlow(StepScreenUiState())
  val stepUiState: StateFlow<StepScreenUiState> = _stepUiState.asStateFlow()

  private val _ingredientUiState = MutableStateFlow(IngredientScreenUiState())
  val ingredientUiState: StateFlow<IngredientScreenUiState> = _ingredientUiState.asStateFlow()

  fun initInfoUiState(recipe: Recipe) {
    _infoUiState.update { s ->
      s.copy(
        id = recipe.id,
        name = recipe.name,
        category = recipe.category,
        subCategory = recipe.subCategory,
        servings = recipe.servings,
        chapters = recipe.chapters,
        unsavedChanges = false
      )
    }
  }

  fun initChapterUiState(chapter: Recipe.Chapter?, index: Int) {
    val value = chapter ?: Recipe.Chapter("", id = UUID(0, 0))

    _chapterUiState.update { s ->
      s.copy(
        id = value.id,
        index = index,
        name = value.name,
        steps = value.steps,
        unsavedChanges = false
      )
    }
  }

  fun initStepUiState(step: Recipe.Chapter.Step?, index: Int) {
    val value = step ?: Recipe.Chapter.Step("", id = UUID(0, 0))

    _stepUiState.update { s ->
      s.copy(
        index = index,
        id = value.id,
        description = value.description,
        ingredients = value.ingredients,
        unsavedChanges = false
      )
    }
  }

  fun initIngredientUiState(ingredient: Recipe.Ingredient?, index: Int) {
    val value = ingredient ?: Recipe.Ingredient("", 0f, "", id = UUID(0, 0))

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

  fun addOrUpdateChapter(chapter: Recipe.Chapter, index: Int) {
    val chapters = _infoUiState.value.chapters.toMutableList()

    if (chapters.elementAtOrNull(index) != null) {
      chapters[index] = chapter
    } else {
      chapters.add(chapter)
    }

    _infoUiState.update { s ->
      s.copy(
        chapters = chapters.toSet(),
        unsavedChanges = true
      )
    }
  }

  fun addOrUpdateStep(step: Recipe.Chapter.Step, index: Int) {
    val steps = _chapterUiState.value.steps.toMutableList()

    if (steps.elementAtOrNull(index) != null) {
      steps[index] = step
    } else {
      steps.add(step)
    }

    _chapterUiState.update { s ->
      s.copy(
        steps = steps.toSet(),
        unsavedChanges = true
      )
    }
  }

  fun addOrUpdateIngredient(ingredient: Recipe.Ingredient, index: Int) {
    val ingredients = _stepUiState.value.ingredients.toMutableList()

    if (ingredients.elementAtOrNull(index) != null) {
      ingredients[index] = ingredient
    } else {
      ingredients.add(ingredient)
    }

    _stepUiState.update { s ->
      s.copy(
        ingredients = ingredients.toSet(),
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