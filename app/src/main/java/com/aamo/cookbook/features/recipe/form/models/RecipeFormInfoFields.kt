package com.aamo.cookbook.features.recipe.form.models

import com.aamo.cookbook.utility.extensions.general.EMPTY

data class RecipeFormInfoFields(
  val name: String = String.EMPTY,
  val category: String = String.EMPTY,
  val subCategory: String = String.EMPTY,
  val servings: Int = 1,
  val note: String = String.EMPTY,
  val chapters: List<RecipeFormChapterFields> = emptyList()
) {
  companion object
}