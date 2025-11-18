@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.test_utility

import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients

@Deprecated(
  "Use RecipeMocker", replaceWith = ReplaceWith(expression = "RecipeMocker")
)
class Mocker {
  companion object {
    fun mockRecipeList(): List<RecipeWithChaptersStepsAndIngredients> {
      var currentRecipeId = 1L
      var currentChapterId = 1L
      var currentStepId = 1L
      var currentIngredientId = 1L

      return (1..5).map { ri ->
        RecipeWithChaptersStepsAndIngredients(
          recipe = Recipe(
            id = currentRecipeId,
            name = "recipe $ri",
            category = "category",
            subCategory = "sub $ri",
            servings = ri
          ), chapters = (1..3).map { ci ->
            ChapterWithStepsAndIngredients(
              chapter = Chapter(
                id = currentChapterId,
                orderNumber = ci,
                name = "chapter $ci",
                recipeId = currentRecipeId
              ), steps = (1..3).map { si ->
                StepWithIngredients(
                  step = Step(
                    id = currentStepId,
                    orderNumber = si,
                    description = "step $si",
                    chapterId = currentChapterId
                  ), ingredients = (1..3).map { ii ->
                    Ingredient(
                      id = currentIngredientId,
                      name = "ingredient $ii",
                      amount = ii.toDouble(),
                      unit = "unit",
                      stepId = currentStepId
                    ).also { currentIngredientId++ }
                  }).also { currentStepId++ }
              }).also { currentChapterId++ }
          }).also { currentRecipeId++ }
      }
    }
  }
}