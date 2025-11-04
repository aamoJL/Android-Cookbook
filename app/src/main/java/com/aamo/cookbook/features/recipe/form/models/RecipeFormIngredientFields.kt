package com.aamo.cookbook.features.recipe.form.models

import com.aamo.cookbook.utility.extensions.general.EMPTY
import java.util.UUID

data class RecipeFormIngredientFields(
  val uuid: UUID = UUID.randomUUID(),
  val name: String = String.EMPTY,
  val amount: Float = 0f,
  val unit: String = String.EMPTY,
)