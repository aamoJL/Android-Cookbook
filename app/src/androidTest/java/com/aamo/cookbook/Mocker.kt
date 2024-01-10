package com.aamo.cookbook

import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients

class Mocker {
  companion object {
    fun mockRecipeList(): List<RecipeWithChaptersStepsAndIngredients> {
      var currentRecipeId = 1
      var currentChapterId = 1
      var currentStepId = 1
      var currentIngredientId = 1

      return (1..5).map { ri ->
        RecipeWithChaptersStepsAndIngredients(
          value = Recipe(currentRecipeId, "recipe", "category", "", ri),
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