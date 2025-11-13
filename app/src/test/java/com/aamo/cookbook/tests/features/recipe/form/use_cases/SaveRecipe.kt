package com.aamo.cookbook.tests.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.toDao
import com.aamo.cookbook.test_utility.RecipeMocker
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class SaveRecipe {
  @Test
  fun `saveData called with correct model`() = runTest {
    val model = RecipeMocker.getFullMocker().mock()
    var actual: RecipeWithChaptersStepsAndIngredients? = null

    saveRecipe(recipe = model) { actual = it; 1 }

    Assert.assertEquals(model, actual)
  }

  @Test
  fun `returns correct id`() = runTest {
    val expected = 0L to 1L
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = expected.first) }.mock()

    val actual = saveRecipe(recipe = model) { expected.second }

    Assert.assertEquals(expected.second, actual)
  }

  @Test
  fun `RecipeFormInfoFields toDao returns correct model`() {
    val model = RecipeMocker.getFullMocker().modify { it.copy(id = 4L) }.mock()

    val info = RecipeFormInfoFields(
      name = model.recipe.name,
      category = model.recipe.category,
      subCategory = model.recipe.subCategory,
      servings = model.recipe.servings,
      note = model.recipe.note,
      chapters = model.chapters.map { c ->
        RecipeFormChapterFields(
          name = c.chapter.name, note = c.chapter.note, steps = c.steps.map { s ->
            RecipeFormStepFields(
              description = s.step.description,
              timerMinutes = s.step.timerMinutes,
              note = s.step.note,
              ingredients = s.ingredients.map { i ->
                RecipeFormIngredientFields(name = i.name, amount = i.amount, unit = i.unit)
              })
          })
      })

    val actual = info.toDao(id = model.recipe.id, thumbnailUri = model.recipe.thumbnailUri)

    Assert.assertEquals(model, actual)
  }
}