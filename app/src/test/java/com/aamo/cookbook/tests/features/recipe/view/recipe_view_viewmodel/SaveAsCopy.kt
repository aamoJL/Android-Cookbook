package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveAsCopy : UnconfinedTest() {
  @Test
  fun `returns original model`() = runTest {
    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    var actual: RecipeWithChaptersStepsAndIngredients? = null
    val viewmodel = RecipeViewViewModel(
      fetchData = {
      flow {
        emit(
          RecipeViewRecipeModel(
            recipe = recipe, bookmark = null, rating = null
          )
        )
      }
    },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      saveAsCopy = { actual = it })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    viewmodel.saveAsCopy()

    TestCase.assertEquals(recipe, actual)
  }
}