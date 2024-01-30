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
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class EditRecipeViewModelTest {
  private var repository = TestRecipeRepository()

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  /**
   * Returns viewmodel with an existing recipe
   */
  private fun withExistingRecipe(): EditRecipeViewModel {
    val savedState = SavedStateHandle(mapOf(Screen.Recipe.argumentName to 1))
    return EditRecipeViewModel(repository, savedState).apply { init() }
  }

  /**
   * Returns viewmodel with a new recipe
   */
  private fun withNewRecipe() : EditRecipeViewModel {
    val savedState = SavedStateHandle(mapOf(Screen.Recipe.argumentName to 0))
    return EditRecipeViewModel(repository, savedState).apply { init() }
  }

  @Test
  fun initialState() = runTest {
    withNewRecipe().also { viewModel ->
      val expected = EditRecipeViewModel.InfoScreenUiState(
        categorySuggestions = repository.getCategoriesWithSubCategories()
      )
      val actual = viewModel.infoUiState.value

      assertEquals(expected, actual)
      assertFalse(viewModel.infoUiState.value.canBeSaved)
    }

    withExistingRecipe().also { viewModel ->
      val recipe = viewModel.infoUiState.value.toRecipeWithChaptersStepsAndIngredients()
      val expected = EditRecipeViewModel.InfoScreenUiState(
        id = recipe.value.id,
        formState = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
          name = recipe.value.name,
          category = recipe.value.category,
          subCategory = recipe.value.subCategory,
          servings = recipe.value.servings,
        ),
        chapters = recipe.chapters,
        categorySuggestions = repository.getCategoriesWithSubCategories(),
        unsavedChanges = false
      )
      val result = viewModel.infoUiState.value
      assertEquals(expected, result)
      assert(viewModel.infoUiState.value.canBeSaved)
    }
  }

  @Test
  fun initChapterUiState() {
    withNewRecipe().also { viewModel ->
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
      assert(viewModel.chapterUiState.value.canBeSaved)
    }
  }

  @Test
  fun initStepUiState() {
    withNewRecipe().also { viewModel ->
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
      assert(viewModel.stepUiState.value.canBeSaved)
    }
  }

  @Test
  fun initIngredientUiState() {
    withNewRecipe().also { viewModel ->
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
      assert(viewModel.ingredientUiState.value.canBeSaved)
    }
  }

  @Test
  fun addOrUpdateChapter_add() {
    withNewRecipe().also { viewModel ->
      val oldUiState = viewModel.infoUiState.value
      val count = 5
      val chapters = List(count){
        ChapterWithStepsAndIngredients(Chapter(name = "name $it"))
      }

      chapters.forEach {
        viewModel.addOrUpdateChapter(it)
      }

      val expected = oldUiState.copy(
        chapters = chapters.mapIndexed { i, chapter ->
          chapter.copy(value = chapter.value.copy(orderNumber = i + 1))
        },
        unsavedChanges = true
      )
      val actual = viewModel.infoUiState.value

      assertEquals(expected, actual)
    }
  }


  @Test
  fun addOrUpdateChapter_update() {
    withExistingRecipe().also { viewModel ->
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
  }

  @Test
  fun addOrUpdateStep_add() {
    withNewRecipe().also { viewModel ->
      val oldUiState = viewModel.chapterUiState.value
      val count = 5
      val steps = List(count){
          StepWithIngredients(Step(description = "new $it"))
      }

      steps.forEach {
        viewModel.addOrUpdateStep(it)
      }

      val expected = oldUiState.copy(
        steps = steps.mapIndexed { i, step ->
          step.copy(value = step.value.copy(orderNumber = i + 1))
        },
        unsavedChanges = true
      )
      val result = viewModel.chapterUiState.value

      assertEquals(expected, result)
    }
  }

  @Test
  fun addOrUpdateStep_update() {
    withExistingRecipe().also { viewModel ->
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
  }

  @Test
  fun addOrUpdateIngredient_add() {
    withNewRecipe().also { viewModel ->
      val oldUiState = viewModel.stepUiState.value
      val count = 5
      val ingredients = List(count){
        Ingredient(name = "name $it")
      }

      ingredients.forEach {
        viewModel.addOrUpdateIngredient(it, -1)
      }

      val expected = oldUiState.copy(
        ingredients = ingredients,
        unsavedChanges = true
      )
      val result = viewModel.stepUiState.value

      assertEquals(expected, result)
    }
  }

  @Test
  fun addOrUpdateIngredient_update() {
    withExistingRecipe().also { viewModel ->
      viewModel.initStepUiState(viewModel.infoUiState.value.chapters.first().steps.first())

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

  @Test
  fun setInfoFormState() {
    withNewRecipe().also { viewModel ->
      val oldState = viewModel.infoUiState.value
      val value = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
        name = "new name",
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

      assertEquals(expected, actual)
    }
  }

  @Test
  fun setChapterFormState() {
    withNewRecipe().also { viewModel ->
      val oldState = viewModel.chapterUiState.value
      val value = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
        name = "new name",
      )

      viewModel.setChapterFormState(value)

      val expected = oldState.copy(
        formState = value,
        unsavedChanges = true
      )
      val actual = viewModel.chapterUiState.value

      assertEquals(expected, actual)
    }
  }

  @Test
  fun setStepFormState() {
    withNewRecipe().also { viewModel ->
      val oldState = viewModel.stepUiState.value
      val value = EditRecipeViewModel.StepScreenUiState.StepFormState(
        description = "new desc"
      )

      viewModel.setStepFormState(value)

      val expected = oldState.copy(
        formState = value,
        unsavedChanges = true
      )
      val actual = viewModel.stepUiState.value

      assertEquals(expected, actual)
    }
  }

  @Test
  fun setIngredientFormState() {
    withNewRecipe().also { viewModel ->
      val oldState = viewModel.ingredientUiState.value
      val value = EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
        name = "new name",
        amount = 15.2f,
        unit = "new unit"
      )

      viewModel.setIngredientFormState(value)

      val expected = oldState.copy(
        formState = value,
        unsavedChanges = true
      )
      val actual = viewModel.ingredientUiState.value

      assertEquals(expected, actual)
    }
  }

  @Test
  fun deleteIngredientTest() {
    withExistingRecipe().also { viewModel ->
      viewModel.initStepUiState(viewModel.infoUiState.value.chapters.first().steps.first())
      val ingredients = viewModel.stepUiState.value.ingredients
      viewModel.deleteIngredient(ingredients.first())

      val expected = ingredients.drop(1)
      val actual = viewModel.stepUiState.value.ingredients

      assertEquals(expected, actual)
    }
  }

  @Test
  fun chapterScreenUiState_toChapterWithStepsAndIngredients() {
    withNewRecipe().also { viewModel ->
      val chapter = TestRecipeRepository.Data
        .recipes.first().chapters.first().let {
          it.copy(value = it.value.copy(recipeId = 0))
        }
      viewModel.initChapterUiState(chapter)

      val actual = viewModel.chapterUiState.value.toChapterWithStepsAndIngredients()
      assertEquals(chapter, actual)
    }
  }

  @Test
  fun stepScreenUiState_toStepWithIngredients() {
    withNewRecipe().also { viewModel ->
      val step = TestRecipeRepository.Data
        .recipes.first().chapters.first().steps.first().let {
          it.copy(value = it.value.copy(chapterId = 0))
        }
      viewModel.initStepUiState(step)

      val actual = viewModel.stepUiState.value.toStepWithIngredients()
      assertEquals(step, actual)
    }
  }

  @Test
  fun ingredientScreenUiState_toIngredient() {
    withNewRecipe().also { viewModel ->
      val ingredient = TestRecipeRepository.Data
        .recipes.first().chapters.first().steps.first().ingredients.first().copy(stepId = 0)
      viewModel.initIngredientUiState(ingredient, 0)

      val actual = viewModel.ingredientUiState.value.toIngredient()
      assertEquals(ingredient, actual)
    }
  }

  @Test
  fun deleteChapter() {
    withExistingRecipe().also { viewModel ->
      val expected = viewModel.infoUiState.value.chapters.toMutableList().apply { removeAt(1) }
        .mapIndexed { index, chapter -> chapter.copy(
          value = chapter.value.copy(orderNumber = index + 1)) }
      viewModel.deleteChapter(viewModel.infoUiState.value.chapters[1])
      val actual = viewModel.infoUiState.value.chapters
      assertEquals(expected, actual)
    }
  }

  @Test
  fun deleteStep() {
    withExistingRecipe().also { viewModel ->
      viewModel.initChapterUiState(viewModel.infoUiState.value.chapters.first())

      val expected = viewModel.chapterUiState.value.steps.toMutableList().apply { removeAt(1) }
        .mapIndexed { index, step -> step.copy(
          value = step.value.copy(orderNumber = index + 1)) }
      viewModel.deleteStep(viewModel.chapterUiState.value.steps[1])
      val actual = viewModel.chapterUiState.value.steps
      assertEquals(expected, actual)
    }
  }

  @Test
  fun swapChapterPositions() {
    withExistingRecipe().also { viewModel ->
      // swap indexes 1 and 2
      val expected = viewModel.infoUiState.value.chapters.toMutableList().also { list ->
        list[1] = list[2].also { list[2] = list[1] }
      }
      viewModel.swapChapterPositions(1,2)
      val actual = viewModel.infoUiState.value.chapters
      assertEquals(expected, actual)
    }
  }

  @Test
  fun swapStepPositions() {
    withExistingRecipe().also { viewModel ->
      viewModel.initChapterUiState(viewModel.infoUiState.value.chapters.first())

      // swap indexes 1 and 2
      val expected = viewModel.chapterUiState.value.steps.toMutableList().also { list ->
        list[1] = list[2].also { list[2] = list[1] }
      }
      viewModel.swapStepPositions(1,2)
      val actual = viewModel.chapterUiState.value.steps
      assertEquals(expected, actual)
    }
  }

  @Test
  fun swapIngredientPositions() {
    withExistingRecipe().also { viewModel ->
      viewModel.initStepUiState(viewModel.infoUiState.value.chapters.first().steps.first())

      // swap indexes 1 and 2
      val expected = viewModel.stepUiState.value.ingredients.toMutableList().also { list ->
        list[1] = list[2].also { list[2] = list[1] }
      }
      viewModel.swapIngredientPositions(1,2)
      val actual = viewModel.stepUiState.value.ingredients
      assertEquals(expected, actual)
    }
  }
}