package com.aamo.cookbook.features.recipe.form.models

import com.aamo.cookbook.utility.extensions.general.EMPTY
import java.util.UUID

data class RecipeFormStepFields(
  val uuid: UUID = UUID.randomUUID(),
  val description: String = String.EMPTY,
  val timerMinutes: Int? = null,
  val note: String = String.EMPTY,
  val ingredients: List<RecipeFormIngredientFields> = emptyList()
)