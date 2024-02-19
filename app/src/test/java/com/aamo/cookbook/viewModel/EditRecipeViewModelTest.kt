package com.aamo.cookbook.viewModel

import com.aamo.cookbook.MainDispatcherRule
import com.aamo.cookbook.database.repository.TestRecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    return EditRecipeViewModel(repository).apply { init(1) }
  }

  /**
   * Returns viewmodel with a new recipe
   */
  private fun withNewRecipe(): EditRecipeViewModel {
    return EditRecipeViewModel(repository).apply { init(0) }
  }

  @Test
  fun initInfoState() = runTest {
    withNewRecipe().also { viewModel ->
      assertTrue(viewModel.infoUiState.value.isNewRecipe)
      assertEquals(
        EditRecipeViewModel.InfoScreenUiState.InfoFormState(),
        viewModel.infoUiState.value.formState
      )
      assertTrue(viewModel.infoUiState.value.chapters.isEmpty())
      assertEquals(
        repository.getCategoriesWithSubCategories(),
        viewModel.infoUiState.value.categorySuggestions
      )
      assertFalse(viewModel.infoUiState.value.canBeSaved)
      assertFalse(viewModel.infoUiState.value.unsavedChanges)
    }

    withExistingRecipe().also { viewModel ->
      val recipe = TestRecipeRepository.Data.recipes.first()

      assertFalse(viewModel.infoUiState.value.isNewRecipe)
      assertEquals(
        EditRecipeViewModel.InfoScreenUiState.InfoFormState(
          name = recipe.value.name,
          category = recipe.value.category,
          subCategory = recipe.value.subCategory,
          servings = recipe.value.servings,
          note = recipe.value.note
        ), viewModel.infoUiState.value.formState
      )
      assertEquals(recipe.chapters, viewModel.infoUiState.value.chapters.map { it.second })
      assertEquals(
        repository.getCategoriesWithSubCategories(),
        viewModel.infoUiState.value.categorySuggestions
      )
      assertTrue(viewModel.infoUiState.value.canBeSaved)
      assertFalse(viewModel.infoUiState.value.unsavedChanges)
    }
  }

  @Test
  fun initChapterUiState() {
    withExistingRecipe().apply { initChapterUiState(-1) }.also { viewModel ->
      assertTrue(viewModel.chapterUiState.value.isNewChapter)
      assertFalse(viewModel.chapterUiState.value.canBeSaved)
      assertEquals(viewModel.infoUiState.value.chapters.size, viewModel.chapterUiState.value.index)
      assertEquals(
        EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(),
        viewModel.chapterUiState.value.formState
      )
      assertTrue(viewModel.chapterUiState.value.steps.isEmpty())
      assertFalse(viewModel.chapterUiState.value.unsavedChanges)
    }

    withExistingRecipe().apply { initChapterUiState(0) }.also { viewModel ->
      val chapter = TestRecipeRepository.Data.recipes.first().chapters.first()

      assertFalse(viewModel.chapterUiState.value.isNewChapter)
      assertTrue(viewModel.chapterUiState.value.canBeSaved)
      assertEquals(0, viewModel.chapterUiState.value.index)
      assertEquals(
        EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
          name = chapter.value.name,
          note = chapter.value.note
        ), viewModel.chapterUiState.value.formState
      )
      assertEquals(chapter.steps, viewModel.chapterUiState.value.steps.map { it.second })
      assertFalse(viewModel.chapterUiState.value.unsavedChanges)
    }
  }

  @Test
  fun initStepUiState() {
    withExistingRecipe().apply {
      initChapterUiState(-1)
      initStepUiState(-1)
    }.also { viewModel ->
      assertTrue(viewModel.stepUiState.value.isNewStep)
      assertFalse(viewModel.stepUiState.value.canBeSaved)
      assertEquals(viewModel.chapterUiState.value.steps.size, viewModel.stepUiState.value.index)
      assertEquals(
        EditRecipeViewModel.StepScreenUiState.StepFormState(),
        viewModel.stepUiState.value.formState
      )
      assertTrue(viewModel.stepUiState.value.ingredients.isEmpty())
      assertFalse(viewModel.stepUiState.value.unsavedChanges)
    }

    withExistingRecipe().apply {
      initChapterUiState(0)
      initStepUiState(0)
    }.also { viewModel ->
      val step = TestRecipeRepository.Data.recipes.first().chapters.first().steps.first()

      assertFalse(viewModel.stepUiState.value.isNewStep)
      assertTrue(viewModel.stepUiState.value.canBeSaved)
      assertEquals(0, viewModel.stepUiState.value.index)
      assertEquals(
        EditRecipeViewModel.StepScreenUiState.StepFormState(
          description = step.value.description,
          timerMinutes = step.value.timerMinutes,
          note = step.value.note
        ),
        viewModel.stepUiState.value.formState
      )
      assertEquals(step.ingredients, viewModel.stepUiState.value.ingredients.map { it.second })
      assertFalse(viewModel.stepUiState.value.unsavedChanges)
    }
  }

  @Test
  fun initIngredientUiState() {
    withExistingRecipe().apply {
      initChapterUiState(-1)
      initStepUiState(-1)
      initIngredientUiState(-1)
    }.also { viewModel ->
      assertTrue(viewModel.ingredientUiState.value.isNewIngredient)
      assertFalse(viewModel.ingredientUiState.value.canBeSaved)
      assertEquals(viewModel.stepUiState.value.ingredients.size, viewModel.ingredientUiState.value.index)
      assertEquals(
        EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(),
        viewModel.ingredientUiState.value.formState
      )
      assertFalse(viewModel.ingredientUiState.value.unsavedChanges)
    }

    withExistingRecipe().apply {
      initChapterUiState(0)
      initStepUiState(0)
      initIngredientUiState(0)
    }.also { viewModel ->
      val ingredient = TestRecipeRepository.Data.recipes
        .first().chapters.first().steps.first().ingredients.first()

      assertFalse(viewModel.ingredientUiState.value.isNewIngredient)
      assertTrue(viewModel.ingredientUiState.value.canBeSaved)
      assertEquals(0, viewModel.ingredientUiState.value.index)
      assertEquals(
        EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
          name = ingredient.name,
          unit = ingredient.unit,
          amount = ingredient.amount
        ),
        viewModel.ingredientUiState.value.formState
      )
      assertFalse(viewModel.ingredientUiState.value.unsavedChanges)
    }
  }

  @Test
  fun applyChapterChanges() {
    val newFormState = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
      name = "Name Updated",
      note = "Note Updated"
    )

    withNewRecipe().apply {
      initChapterUiState(-1)
      setChapterFormState(newFormState)
      applyChapterChanges()
    }.also { viewModel ->
      assertEquals(
        newFormState, EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
          name = viewModel.infoUiState.value.chapters.first().second.value.name,
          note = viewModel.infoUiState.value.chapters.first().second.value.note,
        )
      )
    }

    withExistingRecipe().apply {
      initChapterUiState(0)
      setChapterFormState(newFormState)
      applyChapterChanges()
    }.also { viewModel ->
      assertEquals(
        newFormState, EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
          name = viewModel.infoUiState.value.chapters.first().second.value.name,
          note = viewModel.infoUiState.value.chapters.first().second.value.note,
        )
      )
    }
  }

  @Test
  fun applyStepChanges() {
    val newFormState = EditRecipeViewModel.StepScreenUiState.StepFormState(
      description = "Description Updated",
      timerMinutes = 5,
      note = "Note Updated"
    )

    withNewRecipe().apply {
      initChapterUiState(-1)
      initStepUiState(-1)
      setStepFormState(newFormState)
      applyStepChanges()
    }.also { viewModel ->
      assertEquals(
        newFormState, EditRecipeViewModel.StepScreenUiState.StepFormState(
          description = viewModel.chapterUiState.value.steps.first().second.value.description,
          timerMinutes = viewModel.chapterUiState.value.steps.first().second.value.timerMinutes,
          note = viewModel.chapterUiState.value.steps.first().second.value.note,
        )
      )
    }

    withExistingRecipe().apply {
      initChapterUiState(0)
      initStepUiState(0)
      setStepFormState(newFormState)
      applyStepChanges()
    }.also { viewModel ->
      assertEquals(
        newFormState, EditRecipeViewModel.StepScreenUiState.StepFormState(
          description = viewModel.chapterUiState.value.steps.first().second.value.description,
          timerMinutes = viewModel.chapterUiState.value.steps.first().second.value.timerMinutes,
          note = viewModel.chapterUiState.value.steps.first().second.value.note,
        )
      )
    }
  }

  @Test
  fun applyIngredientChanges() {
    val newFormState = EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
      name = "Name Updated",
      amount = 5f,
      unit = "Unit Updated"
    )

    withNewRecipe().apply {
      initChapterUiState(-1)
      initStepUiState(-1)
      initIngredientUiState(-1)
      setIngredientFormState(newFormState)
      applyIngredientChanges()
    }.also { viewModel ->
      assertEquals(
        newFormState, EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
          name = viewModel.stepUiState.value.ingredients.first().second.name,
          amount = viewModel.stepUiState.value.ingredients.first().second.amount,
          unit = viewModel.stepUiState.value.ingredients.first().second.unit
        )
      )
    }
  }

  @Test
  fun setInfoFormState() {
    val newFormState = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
      name = "Name Updated",
      category = "Category Updated",
      subCategory = "Subcategory Updated",
      servings = 5,
      note = "Note Updated"
    )

    withNewRecipe().apply { setInfoFormState(newFormState) }.also { viewModel ->
      assertEquals(newFormState, viewModel.infoUiState.value.formState)
    }
  }

  @Test
  fun setChapterFormState() {
    val newFormState = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
      name = "Name Updated",
      note = "Note Updated"
    )

    withNewRecipe().apply {
      setChapterFormState(newFormState)
    }.also { viewModel ->
      assertEquals(newFormState, viewModel.chapterUiState.value.formState)
    }
  }

  @Test
  fun setStepFormState() {
    val newFormState = EditRecipeViewModel.StepScreenUiState.StepFormState(
      description = "Description Updated",
      timerMinutes = 5,
      note = "Note Updated"
    )

    withNewRecipe().apply {
      setStepFormState(newFormState)
    }.also { viewModel ->
      assertEquals(newFormState, viewModel.stepUiState.value.formState)
    }
  }

  @Test
  fun setIngredientFormState() {
    val newFormState = EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
      name = "Name Updated",
      amount = 5f,
      unit = "Unit Updated"
    )

    withNewRecipe().apply {
      setIngredientFormState(newFormState)
    }.also { viewModel ->
      assertEquals(newFormState, viewModel.ingredientUiState.value.formState)
    }
  }

  @Test
  fun deleteChapter() {
    withExistingRecipe().apply {
      deleteChapter(1)
    }.also { viewModel ->
      val recipe = TestRecipeRepository.Data.recipes.first()
      assertEquals(
        recipe.chapters.toMutableList().apply { removeAt(1) },
        viewModel.infoUiState.value.chapters.map { it.second })
    }
  }

  @Test
  fun deleteStep() {
    withExistingRecipe().apply {
      initChapterUiState(0)
      deleteStep(1)
    }.also { viewModel ->
      val chapter = TestRecipeRepository.Data.recipes.first().chapters.first()
      assertEquals(
        chapter.steps.toMutableList().apply { removeAt(1) },
        viewModel.chapterUiState.value.steps.map { it.second })
    }
  }

  @Test
  fun deleteIngredientTest() {
    withExistingRecipe().apply {
      initChapterUiState(0)
      initStepUiState(0)
      deleteIngredient(1)
    }.also { viewModel ->
      val step = TestRecipeRepository.Data.recipes.first().chapters.first().steps.first()
      assertEquals(
        step.ingredients.toMutableList().apply { removeAt(1) },
        viewModel.stepUiState.value.ingredients.map { it.second })
    }
  }

  @Test
  fun swapChapterPositions() {
    withExistingRecipe().apply {
      swapChapterPositions(1, 2)
    }.also { viewModel ->
      // swap indexes 1 and 2
      val expected = TestRecipeRepository.Data.recipes
        .first().chapters.toMutableList().also { list ->
          list[1] = list[2].also { list[2] = list[1] }
        }
      val actual = viewModel.infoUiState.value.chapters.map { it.second }
      assertEquals(expected, actual)
    }
  }

  @Test
  fun swapStepPositions() {
    withExistingRecipe().apply {
      initChapterUiState(0)
      swapStepPositions(1, 2)
    }.also { viewModel ->
      // swap indexes 1 and 2
      val expected = TestRecipeRepository.Data.recipes
        .first().chapters.first().steps.toMutableList().also { list ->
          list[1] = list[2].also { list[2] = list[1] }
        }
      val actual = viewModel.chapterUiState.value.steps.map { it.second }
      assertEquals(expected, actual)
    }
  }

  @Test
  fun swapIngredientPositions() {
    withExistingRecipe().apply {
      initChapterUiState(0)
      initStepUiState(0)
      swapIngredientPositions(1, 2)
    }.also { viewModel ->
      // swap indexes 1 and 2
      val expected = TestRecipeRepository.Data.recipes
        .first().chapters.first().steps.first().ingredients.toMutableList().also { list ->
          list[1] = list[2].also { list[2] = list[1] }
        }
      val actual = viewModel.stepUiState.value.ingredients.map { it.second }
      assertEquals(expected, actual)
    }
  }
}