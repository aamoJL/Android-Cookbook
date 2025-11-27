package com.aamo.cookbook.features.recipe.form.models

import com.aamo.cookbook.utility.extensions.general.EMPTY
import java.util.UUID

data class RecipeFormChapterFields(
  val uuid: UUID = UUID.randomUUID(),
  val name: String = String.EMPTY,
  val note: String = String.EMPTY,
  val steps: List<RecipeFormStepFields> = emptyList()
)