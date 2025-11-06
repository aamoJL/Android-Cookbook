package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields

suspend fun saveRecipe(
  recipe: RecipeWithChaptersStepsAndIngredients,
  saveData: suspend (RecipeWithChaptersStepsAndIngredients) -> Int?
): Int? {
  return saveData(recipe)
}

fun RecipeFormInfoFields.toDao(
  id: Int, thumbnailUri: String
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
    ), chapters = this.chapters.map { c ->
      ChapterWithStepsAndIngredients(
        chapter = Chapter(name = c.name, recipeId = id, note = c.note), steps = c.steps.map { s ->
          StepWithIngredients(
            step = Step(
              description = s.description,
              chapterId = 0,
              timerMinutes = s.timerMinutes,
              note = s.note
            ), ingredients = s.ingredients.map { ing ->
              Ingredient(name = ing.name, amount = ing.amount ?: 0f, unit = ing.unit, stepId = 0)
            })
        })
    })
}