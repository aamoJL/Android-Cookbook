package com.aamo.cookbook

import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients

class Mocker {
  companion object {
    fun mockRecipeList(): List<RecipeWithChaptersStepsAndIngredients> {
      var currentRecipeId = 1
      var currentChapterId = 1
      var currentStepId = 1
      var currentIngredientId = 1

      return (1..5).map { ri ->
        RecipeWithChaptersStepsAndIngredients(
          value = Recipe(currentRecipeId, "recipe $ri", "category", "sub $ri", ri),
          chapters = (1..3).map { ci ->
            ChapterWithStepsAndIngredients(
              value = Chapter(currentChapterId, ci, "chapter $ci", currentRecipeId),
              steps = (1..3).map { si ->
                StepWithIngredients(
                  value = Step(currentStepId, si, "step $si", currentChapterId),
                  ingredients = (1..3).map { ii ->
                    Ingredient(
                      currentIngredientId, "ingredient $ii", ii.toFloat(), "unit", currentStepId
                    ).also { currentIngredientId++ }
                  }
                ).also { currentStepId++ }
              }
            ).also { currentChapterId++ }
          }
        ).also { currentRecipeId++ }
      }
    }
  }
}