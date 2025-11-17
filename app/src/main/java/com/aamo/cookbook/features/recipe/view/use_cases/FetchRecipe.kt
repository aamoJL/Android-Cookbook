package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.view.models.RecipeViewRecipeModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

fun fetchRecipe(
  fetchRecipe: suspend () -> RecipeWithChaptersStepsAndIngredients,
  fetchBookmark: () -> Flow<RecipeBookmark?>,
  fetchRating: () -> Flow<RecipeRating?>
): Flow<RecipeViewRecipeModel> {
  return flow {
    val recipe = fetchRecipe()

    combine(fetchBookmark(), fetchRating()) { bookmark, rating ->
      RecipeViewRecipeModel(recipe = recipe, bookmark = bookmark, rating = rating)
    }.collect {
      this.emit(it)
    }
  }
}