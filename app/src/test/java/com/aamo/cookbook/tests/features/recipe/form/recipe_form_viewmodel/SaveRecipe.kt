package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
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

@Suppress("HardCodedStringLiteral")
@OptIn(ExperimentalCoroutinesApi::class)
class SaveRecipe : UnconfinedTest() {
  @Test
  fun `passes correct arguments to saveData`() = runTest {
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = 4, thumbnailUri = "Uri") }.mock()

    var actualRecipe: RecipeWithChaptersStepsAndIngredients? = null
    val viewmodel = RecipeFormViewModel(
      fetchData = { model },
      saveData = { recipe -> actualRecipe = recipe },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    )

    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
      viewmodel.recipe.collect()
    }

    val name = "Name"

    viewmodel.formRecipeState.value.fields.name.update(name)

    val expected = model.copy(recipe = model.recipe.copy(name = name))

    viewmodel.saveRecipe()
    Assert.assertEquals(expected, actualRecipe)
  }

  @Test
  fun `does not crash when error`() = runTest {
    RecipeFormViewModel(
      fetchData = { RecipeMocker.getFullMocker().mock() },
      saveData = { _ -> error(String.EMPTY) },
      deleteData = { fail() },
      fetchCategorySuggestions = { emptyMap() },
    ).saveRecipe()
  }
}