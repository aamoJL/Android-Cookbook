package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.utility.extensions.general.EMPTY

suspend fun copyAndSaveRecipe(dao: RecipeDao, recipe: RecipeWithChaptersStepsAndIngredients): Long {
  return dao.upsert(
    recipe.copy(
      recipe = recipe.recipe.copy(id = 0L, thumbnailUri = String.EMPTY),
      chapters = recipe.chapters.map { c ->
        c.copy(chapter = c.chapter.copy(id = 0L, recipeId = 0L), steps = c.steps.map { s ->
          s.copy(
            step = s.step.copy(id = 0L, chapterId = 0L), ingredients = s.ingredients.map { i ->
              i.copy(id = 0L, stepId = 0L)
            })
        })
      })
  )
}