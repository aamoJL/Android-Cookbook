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

class EditRecipeViewModelTestExistingRecipe {
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
        formState = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
          name = recipe.value.name,
          category = recipe.value.category,
          subCategory = recipe.value.subCategory,
          servings = recipe.value.servings,
        ),
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
    viewModel.initChapterUiState(chapter)

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
    viewModel.initStepUiState(step)

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

class EditRecipeViewModelTestNewRecipe {
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
    viewModel.initChapterUiState(chapter)

    val expected = EditRecipeViewModel.ChapterScreenUiState(
      id = chapter.value.id,
      formState = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
        name = chapter.value.name,
      ),
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
    viewModel.initStepUiState(step)

    val expected = EditRecipeViewModel.StepScreenUiState(
      id = step.value.id,
      orderNumber = step.value.orderNumber,
      formState = EditRecipeViewModel.StepScreenUiState.StepFormState(
        description = step.value.description,
      ),
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
      formState = EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
        name = ingredient.name,
        unit = ingredient.unit,
        amount = ingredient.amount,
      ),
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
  fun setInfoFormState_WithoutChapters() {
    val oldState = viewModel.infoUiState.value
    val value = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
      name = "",
      category = "new category",
      subCategory = "new sub",
      servings = 5
    )

    viewModel.setInfoFormState(value)

    val expected = oldState.copy(
      formState = value,
      unsavedChanges = true
    )
    val actual = viewModel.infoUiState.value

    assert(expected.chapters.isEmpty())
    assertEquals(expected, actual)
  }
}