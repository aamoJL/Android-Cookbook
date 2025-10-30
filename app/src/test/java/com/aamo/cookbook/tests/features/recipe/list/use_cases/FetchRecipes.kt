package com.aamo.cookbook.tests.features.recipe.list.use_cases

import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.features.recipe.list.RecipesByCategoryScreenViewModel
import com.aamo.cookbook.features.recipe.list.use_cases.fromDao
import com.aamo.cookbook.model.RecipeBookmark
import com.aamo.cookbook.model.RecipeRating
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FetchRecipes {
  @Test
  fun `returns correct models`() {
    val recipes = listOf(
      RecipeWithBookmarkAndRating(recipe = Recipe(id = 1), bookmark = null, rating = null),
      RecipeWithBookmarkAndRating(
        recipe = Recipe(id = 2), bookmark = RecipeBookmark(recipeId = 2), rating = null
      ),
      RecipeWithBookmarkAndRating(
        recipe = Recipe(id = 3),
        bookmark = null,
        rating = RecipeRating(ratingOutOfFive = 4, recipeId = 3)
      ),
      RecipeWithBookmarkAndRating(
        recipe = Recipe(id = 4),
        bookmark = RecipeBookmark(recipeId = 4),
        rating = RecipeRating(ratingOutOfFive = 2, recipeId = 4)
      ),
    )

    val models = runBlocking {
      RecipesByCategoryScreenViewModel.Model.fromDao {
        flow { emit(recipes) }
      }.first()
    }

    assertEquals(recipes.map {
      RecipesByCategoryScreenViewModel.Model(
        it.recipe, it.rating?.ratingOutOfFive, it.bookmark != null
      )
    }, models)
  }
}