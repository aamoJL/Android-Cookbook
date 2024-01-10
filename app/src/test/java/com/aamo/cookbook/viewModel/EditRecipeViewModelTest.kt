package com.aamo.cookbook.viewModel

import androidx.lifecycle.SavedStateHandle
import com.aamo.cookbook.MainDispatcherRule
import com.aamo.cookbook.Screen
import com.aamo.cookbook.database.repository.TestRecipeRepository
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditRecipeViewModelTestWithId {
  private val recipeId = 1
  private lateinit var viewModel: EditRecipeViewModel
  private lateinit var repository: TestRecipeRepository

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setup() {
    repository = TestRecipeRepository()
    val savedState = SavedStateHandle(mapOf(Screen.Recipe.argumentName to recipeId))
    viewModel = EditRecipeViewModel(TestRecipeRepository(), savedState)
    viewModel.init()
  }

  @Test
  fun init_WithId() = runTest {
    val recipe = repository.getRecipeWithChaptersStepsAndIngredients(recipeId)

    assertNotNull(recipe)

    if(recipe != null){
      val expected = EditRecipeViewModel.InfoScreenUiState(
        id = recipe.value.id,
        name = recipe.value.name,
        category = recipe.value.category,
        subCategory = recipe.value.subCategory,
        servings = recipe.value.servings,
        chapters = recipe.chapters,
        unsavedChanges = false
      )
      val result = viewModel.infoUiState.value
      assertEquals(expected, result)
    }
  }

  @Test
  fun addOrUpdateChapter_update() {
    val oldUiState = viewModel.infoUiState.value
    val chapter = oldUiState.chapters.first().copy(
      value = oldUiState.chapters.first().value.copy(
        name = "updated"
      )
    )
    viewModel.addOrUpdateChapter(chapter)

    val expected = oldUiState.copy(
      chapters = oldUiState.chapters.toMutableSet().map { c ->
        if (c.value.id == chapter.value.id) chapter else c
      },
      unsavedChanges = true)
    val result = viewModel.infoUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun addOrUpdateStep_update() {
    val chapter = viewModel.infoUiState.value.chapters.first()
    viewModel.initChapterUiState(chapter.value, chapter.steps)

    val oldUiState = viewModel.chapterUiState.value
    val step = oldUiState.steps.first().copy(
      value = oldUiState.steps.first().value.copy(
        description = "updated"
      )
    )
    viewModel.addOrUpdateStep(step)

    val expected = oldUiState.copy(
      steps = oldUiState.steps.toMutableSet().map { s ->
        if (s.value.id == step.value.id) step else s
      },
      unsavedChanges = true)
    val result = viewModel.chapterUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun addOrUpdateIngredient_update() {
    val step = viewModel.infoUiState.value.chapters.first().steps.first()
    viewModel.initStepUiState(step.value, step.ingredients)

    val oldUiState = viewModel.stepUiState.value
    val ingredient = oldUiState.ingredients.first().copy(
      name = "updated"
    )
    val index = 0
    viewModel.addOrUpdateIngredient(ingredient, index)

    val expected = oldUiState.copy(
      ingredients = oldUiState.ingredients.toMutableSet().mapIndexed { listIndex, i ->
        if (listIndex == index) ingredient else i
      },
      unsavedChanges = true)
    val result = viewModel.stepUiState.value

    assertEquals(expected, result)
  }
}

class EditRecipeViewModelTestWithoutId {
  private var recipeId = 0
  private lateinit var viewModel: EditRecipeViewModel
  private lateinit var repository: TestRecipeRepository

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setup() {
    repository = TestRecipeRepository()
    val savedState = SavedStateHandle(mapOf(Screen.Recipe.argumentName to recipeId))
    viewModel = EditRecipeViewModel(TestRecipeRepository(), savedState)
    viewModel.init()
  }

  @Test
  fun init() = runTest {
    val expected = EditRecipeViewModel.InfoScreenUiState()
    val actual = viewModel.infoUiState.value

    assertEquals(expected, actual)
  }

  @Test
  fun initChapterUiState() {
    val chapter = TestRecipeRepository.Data.recipes.first().chapters.first()
    viewModel.initChapterUiState(chapter.value, chapter.steps)

    val expected = EditRecipeViewModel.ChapterScreenUiState(
      id = chapter.value.id,
      name = chapter.value.name,
      orderNumber = chapter.value.orderNumber,
      steps = chapter.steps,
      unsavedChanges = false
    )
    val actual = viewModel.chapterUiState.value

    assertEquals(expected, actual)
  }

  @Test
  fun initStepUiState() {
    val step = TestRecipeRepository.Data.recipes.first().chapters.first().steps.first()
    viewModel.initStepUiState(step.value, step.ingredients)

    val expected = EditRecipeViewModel.StepScreenUiState(
      id = step.value.id,
      orderNumber = step.value.orderNumber,
      description = step.value.description,
      ingredients = step.ingredients,
      unsavedChanges = false
    )
    val result = viewModel.stepUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun initIngredientUiState() {
    val ingredient =
      TestRecipeRepository.Data.recipes.first().chapters.first().steps.first().ingredients.first()
    viewModel.initIngredientUiState(ingredient, 0)

    val expected = EditRecipeViewModel.IngredientScreenUiState(
      index = 0,
      id = ingredient.id,
      name = ingredient.name,
      unit = ingredient.unit,
      amount = ingredient.amount,
      unsavedChanges = false
    )
    val result = viewModel.ingredientUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun addOrUpdateChapter_add() {
    val oldUiState = viewModel.infoUiState.value
    val chapter = ChapterWithStepsAndIngredients(Chapter(
      id = 0, 0, "new", 0
    ))
    viewModel.addOrUpdateChapter(chapter)

    val expected = oldUiState.copy(
      chapters = oldUiState.chapters.plus(chapter),
      unsavedChanges = true
    )
    val result = viewModel.infoUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun addOrUpdateStep_add() {
    val oldUiState = viewModel.chapterUiState.value
    val step = StepWithIngredients(Step(description = "new"))
    viewModel.addOrUpdateStep(step)

    val expected = oldUiState.copy(
      steps = oldUiState.steps.plus(step),
      unsavedChanges = true
    )
    val result = viewModel.chapterUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun addOrUpdateIngredient_add() {
    val oldUiState = viewModel.stepUiState.value
    val ingredient = Ingredient(name = "new")
    viewModel.addOrUpdateIngredient(ingredient, -1)

    val expected = oldUiState.copy(
      ingredients = oldUiState.ingredients.plus(ingredient),
      unsavedChanges = true
    )
    val result = viewModel.stepUiState.value

    assertEquals(expected, result)
  }

  @Test
  fun setRecipeName() {
    val value = "new"
    viewModel.setRecipeName(value)

    assertEquals(
      viewModel.infoUiState.value, viewModel.infoUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setCategory() {
    val value = "new"
    viewModel.setCategory(value)

    assertEquals(
      viewModel.infoUiState.value, viewModel.infoUiState.value.copy(
        category = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setSubCategory() {
    val value = "new"
    viewModel.setSubCategory(value)

    assertEquals(
      viewModel.infoUiState.value, viewModel.infoUiState.value.copy(
        subCategory = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setServings() {
    val value = 10
    viewModel.setServings(value)

    assertEquals(
      viewModel.infoUiState.value, viewModel.infoUiState.value.copy(
        servings = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setChapterName() {
    val value = "new"
    viewModel.setChapterName(value)

    assertEquals(
      viewModel.chapterUiState.value, viewModel.chapterUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setStepDescription() {
    val value = "new"
    viewModel.setStepDescription(value)

    assertEquals(
      viewModel.stepUiState.value, viewModel.stepUiState.value.copy(
        description = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientName() {
    val value = "new"
    viewModel.setIngredientName(value)

    assertEquals(
      viewModel.ingredientUiState.value, viewModel.ingredientUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientUnit() {
    val value = "new"
    viewModel.setIngredientUnit(value)

    assertEquals(
      viewModel.ingredientUiState.value, viewModel.ingredientUiState.value.copy(
        unit = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientAmount() {
    val value = 10f
    viewModel.setIngredientAmount(value)

    assertEquals(
      viewModel.ingredientUiState.value, viewModel.ingredientUiState.value.copy(
        amount = value,
        unsavedChanges = true
      )
    )
  }
}