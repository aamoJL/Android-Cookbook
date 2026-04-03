package com.aamo.cookbook.features.recipe.form.use_cases

import android.content.Context
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.utility.extensions.general.EMPTY

fun copyRecipe(
  recipe: RecipeWithChaptersStepsAndIngredients,
  context: Context,
): RecipeWithChaptersStepsAndIngredients {
  return recipe.let { r ->
    r.copy(
      recipe = r.recipe.copy(
        id = 0L,
        thumbnailUri = String.EMPTY,
        name = context.getString(R.string.text_recipe_name_as_copy, r.recipe.name)
      ),
      chapters = r.chapters.map { c ->
        c.copy(
          chapter = c.chapter.copy(id = 0L, recipeId = 0L),
          steps = c.steps.map { s ->
            s.copy(
              step = s.step.copy(id = 0L, chapterId = 0L),
              ingredients = s.ingredients.map { i ->
                i.copy(id = 0L, stepId = 0L)
              },
            )
          },
        )
      },
    )
  }
}