package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.UnconfinedTest
import com.aamo.cookbook.utility.extensions.general.EMPTY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
@OptIn(ExperimentalCoroutinesApi::class)
class SaveRecipe : UnconfinedTest() {
  @Test
  fun `passes correct arguments to saveData`() = runTest {
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = 4, thumbnailUri = "Uri") }.mock()
    val uuid = UUID.randomUUID()
    val expected = RecipeFormInfoFields.fromDao(model).let {
      it.copy(chapters = it.chapters.map { c ->
        c.copy(uuid = uuid, steps = c.steps.map { s ->
          s.copy(uuid = uuid, ingredients = s.ingredients.map { i ->
            i.copy(uuid = uuid)
          })
        })
      })
    }

    var actualFields: RecipeFormInfoFields? = null
    var actualId: Long? = null
    var actualThumbnail: String? = null
    val viewmodel = RecipeFormViewModel(fetchData = { model }, saveData = { fields, id, thumbnail ->
      actualFields = fields.let {
        it.copy(chapters = it.chapters.map { c ->
          c.copy(uuid = uuid, steps = c.steps.map { s ->
            s.copy(uuid = uuid, ingredients = s.ingredients.map { i ->
              i.copy(uuid = uuid)
            })
          })
        })
      }
      actualId = id
      actualThumbnail = thumbnail
    }, deleteData = { fail() })

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    viewmodel.saveRecipe(RecipeFormInfoFields.fromDao(model))

    Assert.assertEquals(model.recipe.id, actualId)
    Assert.assertEquals(model.recipe.thumbnailUri, actualThumbnail)
    Assert.assertEquals(expected, actualFields)
  }

  @Test
  fun `does not crash when error`() = runTest {
    RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _, _, _ -> error(String.EMPTY) },
      deleteData = { fail() }).saveRecipe(RecipeFormInfoFields())
  }
}