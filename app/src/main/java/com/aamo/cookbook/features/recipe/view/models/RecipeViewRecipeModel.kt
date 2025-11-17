package com.aamo.cookbook.features.recipe.view.models

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients

data class RecipeViewRecipeModel(
  val recipe: RecipeWithChaptersStepsAndIngredients,
  val bookmark: RecipeBookmark?,
  val rating: RecipeRating?
)