package com.aamo.cookbook.features.recipe.form.models

import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormIngredientFields(
  val name: String,
  val amount: Float,
  val unit: String,
)