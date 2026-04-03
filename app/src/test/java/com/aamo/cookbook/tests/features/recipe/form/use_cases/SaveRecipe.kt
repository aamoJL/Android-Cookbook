package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.database.DatabaseTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SaveRecipe : DatabaseTest() {
  @Test
  fun `returns correct id`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    val actual = saveRecipe(
      dao = dao,
      recipe = model,
    )

    Assert.assertEquals(1L, actual)
  }

  @Test
  fun `saves correct model`() = runTest {
    val thumbnail = "123.jpg"
    val mocker = RecipeMocker.getFullMocker().modify { it.copy(thumbnailUri = thumbnail) }
    val model = mocker.mock()
    val actual = saveRecipe(
      dao = dao,
      recipe = model,
    ).let { id ->
      dao.getCompleteRecipe(id)
    }

    checkNotNull(actual)
    assertEquals(mocker.withIds().mock(), actual)
  }

  @Test
  fun `updates model`() = runTest {
    val mocker = RecipeMocker.getFullMocker()
    val model = dao.upsert(mocker.mock()).let {
      dao.getCompleteRecipe(it)
    }

    checkNotNull(model)

    val thumbnail = "123.jpg"
    val expected = mocker.withIds(
      recipeId = model.recipe.id,
      chapterId = (model.chapters.size + 1).toLong(),
      stepId = (model.chapters.flatMap { it.steps }.size + 1).toLong(),
      ingredientId = (model.chapters.flatMap { c -> c.steps }
        .flatMap { it.ingredients }.size + 1).toLong()
    ).modify { it.copy(thumbnailUri = thumbnail) }.mock()
    val actual = saveRecipe(
      dao = dao,
      recipe = expected,
    ).let { id ->
      dao.getCompleteRecipe(id)
    }

    checkNotNull(actual)
    assertEquals(model.recipe.id, actual.recipe.id)
    assertEquals(expected, actual)
  }
}