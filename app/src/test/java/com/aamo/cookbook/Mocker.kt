package com.aamo.cookbook

import com.aamo.cookbook.model.Recipe

class Mocker {
  class RecipeMocker {
    fun getExampleRecipe(): Recipe {
      return Recipe(
        "recipe", "category", "subCategory", 2, setOf(
          Recipe.Chapter(
            "chapter", setOf(
              Recipe.Chapter.Step(
                "step", setOf(
                  Recipe.Ingredient("ingredient", 2f, "unit")
                )
              )
            )
          )
        )
      )
    }
  }
}