package com.aamo.cookbook.utility.extensions

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

fun RecipeWithChaptersStepsAndIngredients.asNew(): RecipeWithChaptersStepsAndIngredients {
  return this.copy(recipe = this.recipe.copy(id = 0), chapters = this.chapters.map { c ->
    c.copy(chapter = c.chapter.copy(id = 0, recipeId = 0), steps = c.steps.map { s ->
      s.copy(step = s.step.copy(id = 0, chapterId = 0), ingredients = s.ingredients.map { i ->
        i.copy(id = 0, stepId = 0)
      })
    })
  })
}