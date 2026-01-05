package com.aamo.cookbook.tests.features.recipe.view.recipe_view_viewmodel

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.features.recipe.view.RecipeViewViewModel
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import junit.framework.TestCase
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBookmark : UnconfinedTest() {
  @Test
  fun `updateBookmark returns new when bookmark is null`() = runTest(UnconfinedTestDispatcher()) {
    val model = RecipeViewRecipeModel(
      recipe = RecipeMocker.getFullMocker().withIds().mock(), bookmark = null, rating = null
    )
    var bookmark: RecipeBookmark? = null
    var value: Boolean? = null
    val viewmodel = RecipeViewViewModel(
      fetchData = { flow { emit(model) } },
      updateBookmark = { v, b ->
        value = v
        bookmark = b
      },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.bookmark.collect()
    }

    viewmodel.updateBookmark(true)

    TestCase.assertEquals(RecipeBookmark(recipeId = model.recipe.recipe.id), bookmark)
    TestCase.assertEquals(true, value)
  }

  @Test
  fun `updateBookmark returns old when bookmark is not null`() =
    runTest(UnconfinedTestDispatcher()) {
      val model = RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(),
        bookmark = RecipeBookmark(id = 1L, recipeId = 1L),
        rating = null
      )
      var bookmark: RecipeBookmark? = null
      var value: Boolean? = null
      val viewmodel = RecipeViewViewModel(
        fetchData = { flow { emit(model) } },
        updateBookmark = { v, b ->
          value = v
          bookmark = b
        },
        updateRating = { _, _ -> fail() },
        updateThumbnail = { _, _ -> fail() },
        saveAsCopy = { fail() })

      backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        viewmodel.bookmark.collect()
      }

      viewmodel.updateBookmark(false)

      TestCase.assertEquals(model.bookmark, bookmark)
      TestCase.assertEquals(false, value)
    }

  @Test
  fun `updateBookmark not called when not changed`() = runTest(UnconfinedTestDispatcher()) {
    val dataFlow = MutableSharedFlow<RecipeViewRecipeModel>()
    val viewmodel = RecipeViewViewModel(
      fetchData = { dataFlow },
      updateBookmark = { _, _ -> fail() },
      updateRating = { _, _ -> fail() },
      updateThumbnail = { _, _ -> fail() },
      saveAsCopy = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.bookmark.collect()
    }

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(), bookmark = null, rating = null
      )
    )

    viewmodel.updateBookmark(false)

    dataFlow.emit(
      RecipeViewRecipeModel(
        recipe = RecipeMocker.getFullMocker().withIds().mock(),
        bookmark = RecipeBookmark(recipeId = 1L),
        rating = null
      )
    )

    viewmodel.updateBookmark(true)
  }
}