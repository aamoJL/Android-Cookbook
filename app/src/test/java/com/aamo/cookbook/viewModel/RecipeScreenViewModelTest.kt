package com.aamo.cookbook.viewModel

import androidx.lifecycle.SavedStateHandle
import com.aamo.cookbook.MainDispatcherRule
import com.aamo.cookbook.Screen
import com.aamo.cookbook.database.repository.TestRecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeScreenViewModelTest {
  private val recipeId = 1
  private lateinit var viewModel: RecipeScreenViewModel
  private lateinit var repository: TestRecipeRepository

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setup() = runTest {
    repository = TestRecipeRepository()
    val savedState = SavedStateHandle(mapOf(Screen.Recipe.argumentName to recipeId))
    viewModel = RecipeScreenViewModel(repository, savedState)
    viewModel.init()
  }

  @Test
  fun verifyInit_Recipe() = runTest {
    val expected = repository.getRecipeWithChaptersStepsAndIngredients(recipeId)
    val actual = viewModel.recipe

    assertEquals(expected, actual)
  }

  @Test
  fun verifyInit_Progress() = runTest {
    val expected = repository.getRecipeWithChaptersStepsAndIngredients(recipeId)!!.chapters.map {
      it.steps.map { false }
    }
    val actual = viewModel.chapterPageUiStates.value.map { it.progress }
    assertEquals(expected, actual)
  }

  @Test
  fun verifyInit_FavoriteState() {
    val expected = TestRecipeRepository.Data.favoriteRecipes.firstOrNull { it.recipeId == viewModel.recipe.value.id } != null
    val actual = viewModel.favoriteState.value
    assertEquals(expected, actual)
  }

  @Test
  fun updateProgress() {
    val oldProgress = viewModel.chapterPageUiStates.value.map { it.progress }
    val chapterIndex = 0; val stepIndex = 0
    viewModel.updateProgress(chapterIndex, stepIndex, true)

    val expected = oldProgress.map {
      it.toMutableList()
    }.toMutableList()
    expected[chapterIndex][stepIndex] = true
    val actual = viewModel.chapterPageUiStates.value.map { it.progress }
    assertEquals(expected, actual)
  }

  @Test
  fun setFavoriteState() {
    val initState = viewModel.favoriteState.value

    val expected = !initState
    viewModel.setFavoriteState(expected)
    val actual = viewModel.favoriteState.value
    assertEquals(expected, actual)
  }

  @Test
  fun setServingsCount() {
    val init = viewModel.servingsState.value.baseline
    val current = viewModel.servingsState.value.current
    assertEquals(init, current)

    val expected = current + 1
    viewModel.setServingsCount(expected)
    val actual = viewModel.servingsState.value.current

    assertEquals(expected, actual)
  }
}