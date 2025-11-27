package com.aamo.cookbook.features.recipe.list.models

import com.aamo.cookbook.database.entities.Recipe

data class RecipeListRecipeModel(val recipe: Recipe, val rating: Int?, val isBookmarked: Boolean) {
  companion object
}