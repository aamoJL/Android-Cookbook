package com.aamo.cookbook.features.recipe.form.models

import com.aamo.cookbook.utility.extensions.general.EMPTY

data class RecipeFormChapterFields(
  val name: String = String.EMPTY,
  val note: String = String.EMPTY,
  val steps: List<RecipeFormStepFields> = emptyList()
)