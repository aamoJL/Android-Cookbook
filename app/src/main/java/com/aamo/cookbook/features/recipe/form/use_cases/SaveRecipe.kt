package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.utility.extensions.general.Zero

suspend fun saveRecipe(
  dao: RecipeDao,
  id: Long,
  thumbnailUri: String,
  fields: RecipeFormInfoFields,
): Long {
  return dao.upsert(fields.toDao(id = id, thumbnailUri = thumbnailUri))
}

private fun RecipeFormInfoFields.toDao(
  id: Long, thumbnailUri: String
): RecipeWithChaptersStepsAndIngredients {
  return RecipeWithChaptersStepsAndIngredients(
    recipe = Recipe(
      id = id,
      name = this.name,
      category = this.category,
      subCategory = this.subCategory,
      servings = this.servings,
      note = this.note,
      thumbnailUri = thumbnailUri
    ), chapters = this.chapters.mapIndexed { i, c ->
      ChapterWithStepsAndIngredients(
        chapter = Chapter(name = c.name, recipeId = id, orderNumber = i + 1, note = c.note),
        steps = c.steps.mapIndexed { i, s ->
          StepWithIngredients(
            step = Step(
              description = s.description,
              chapterId = 0,
              orderNumber = i + 1,
              timerMinutes = s.timerMinutes,
              note = s.note
            ), ingredients = s.ingredients.map { ing ->
              Ingredient(
                name = ing.name, amount = ing.amount ?: Double.Zero, unit = ing.unit, stepId = 0
              )
            })
        })
    })
}