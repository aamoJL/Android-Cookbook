package com.aamo.cookbook.tests.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.features.recipe.view.use_cases.fetchRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FetchRecipe {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns correct model`() = runTest(UnconfinedTestDispatcher()) {
    val recipe = RecipeMocker.getFullMocker().mock()
    val bookmark = RecipeBookmark(recipeId = recipe.recipe.id)
    val rating = RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 4)

    val recipeFlow = MutableSharedFlow<RecipeWithChaptersStepsAndIngredients?>()
    val bookmarkFlow = MutableSharedFlow<RecipeBookmark?>()
    val ratingFlow = MutableSharedFlow<RecipeRating?>()
    val useCaseFlow = fetchRecipe(
      fetchRecipe = { recipeFlow },
      fetchBookmark = { bookmarkFlow },
      fetchRating = { ratingFlow })
    var actual: RecipeViewRecipeModel? = null

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      useCaseFlow.collect { actual = it }
    }

    assertNull(actual)

    recipeFlow.emit(recipe)

    bookmarkFlow.emit(bookmark)
    assertNull(actual) // Should be emitted only when bookmark and rating has been both emitted

    ratingFlow.emit(rating)

    val expected = RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating)
    assertEquals(expected, actual)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `returns updated model`() = runTest(UnconfinedTestDispatcher()) {
    val recipe = RecipeMocker.getFullMocker().mock()

    val recipeFlow = MutableSharedFlow<RecipeWithChaptersStepsAndIngredients?>()
    val bookmarkFlow = MutableSharedFlow<RecipeBookmark?>()
    val ratingFlow = MutableSharedFlow<RecipeRating?>()
    val useCaseFlow = fetchRecipe(
      fetchRecipe = { recipeFlow },
      fetchBookmark = { bookmarkFlow },
      fetchRating = { ratingFlow })
    var actual: RecipeViewRecipeModel? = null

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      useCaseFlow.collect { actual = it }
    }

    recipeFlow.emit(recipe)
    bookmarkFlow.emit(RecipeBookmark(recipeId = recipe.recipe.id))
    ratingFlow.emit(RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 4))

    assertNotNull(actual)

    bookmarkFlow.emit(null)

    assertNotNull(actual)
    assertNull(actual?.bookmark)

    ratingFlow.emit(null)

    assertNotNull(actual)
    assertNull(actual?.rating)
  }
}