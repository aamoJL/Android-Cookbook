package com.aamo.cookbook.viewModel

import com.aamo.cookbook.MainDispatcherRule
import com.aamo.cookbook.database.repository.TestRecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeSearchViewModelTest {
  private lateinit var viewModel: RecipeSearchViewModel
  private lateinit var repository: TestRecipeRepository

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Before
  fun setup() = runTest {
    repository = TestRecipeRepository()
    viewModel = RecipeSearchViewModel(repository)
  }

  @Test
  fun verifyInitWord() {
    val expected = ""
    val actual = viewModel.searchWord.value
    assertEquals(expected, actual)
  }

  @Test
  fun verifyInitValidRecipes() {
    val expected = TestRecipeRepository.Data.recipes.map { it.value }
    val actual = viewModel.validRecipes.value.map { it.value }
    assertEquals(expected, actual)
  }

  @Test
  fun setSearchWord() {
    val expected = viewModel.validRecipes.value.first().value.name

    viewModel.setSearchWord(expected)
    assertEquals(1, viewModel.validRecipes.value.size)

    val actual = viewModel.validRecipes.value.first().value.name
    assertEquals(expected, actual)
  }
}