package com.aamo.cookbook.features.recipe.form.models

import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormStepFields(
  val description: String,
  val timerMinutes: Int?,
  val note: String,
  val ingredients: List<RecipeFormIngredientFields>
)