@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.FullFavoriteRecipe
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeCategoryTuple
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithBookmarkAndRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.utility.extensions.general.letIf
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
  // region GET
  @Query("SELECt * FROM recipes WHERE id = :recipeId")
  suspend fun getRecipe(recipeId: Int): Recipe?

  @Query("SELECT DISTINCT category FROM recipes")
  fun getCategoriesFlow(): Flow<List<String>>

  @Query("SELECT category, subCategory FROM recipes")
  suspend fun getCategoriesMap(): Map<@MapColumn(columnName = "category") String, List<@MapColumn(
    columnName = "subCategory"
  ) String>>

  @Transaction
  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipesWithBookmarkAndRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE category = :category ORDER BY name ASC")
  fun getRecipesWithBookmarkAndRatingFlow(category: String): Flow<List<RecipeWithBookmarkAndRating>>

  @Transaction
  @Query(
    """
    SELECT recipes.*, bookmarks.*, ratings.* FROM recipes
    JOIN favoriteRecipes AS bookmarks ON bookmarks.recipeId = recipes.id
    LEFT OUTER JOIN recipeRatings AS ratings ON ratings.recipeId = recipes.id
  """
  )
  fun getBookmarksWithRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>>

  // -------- //

  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipesFlow(): Flow<List<Recipe>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE id = :recipeId")
  @Deprecated(
    message = "This function returns unsorted recipe",
    replaceWith = ReplaceWith("getRecipeWithChaptersStepsAndIngredients(recipeId)")
  )
  suspend fun getRecipeWithChaptersStepsAndIngredientsUnsorted(recipeId: Int): RecipeWithChaptersStepsAndIngredients?

  /**
   * Returns recipes with chapters and steps sorted by order number, and ingredients sorted by with name
   */
  suspend fun getRecipeWithChaptersStepsAndIngredients(recipeId: Int): RecipeWithChaptersStepsAndIngredients? {
    @Suppress("DEPRECATION") return getRecipeWithChaptersStepsAndIngredientsUnsorted(recipeId)?.let { recipe ->
      recipe.copy(chapters = recipe.chapters.sortedBy { it.chapter.orderNumber }.map { chapter ->
        chapter.copy(steps = chapter.steps.sortedBy { it.step.orderNumber }.map { step ->
          step.copy(
            ingredients = step.ingredients.sortedBy { it.name })
        })
      })
    }
  }

  @Query("SELECT DISTINCT category, subCategory FROM recipes")
  suspend fun getCategoriesWithSubcategories(): List<RecipeCategoryTuple>

  @Query("SELECT * FROM recipeRatings WHERE recipeId = :recipeId")
  suspend fun getRecipeRatingById(recipeId: Int): RecipeRating?

  @Transaction
  @Query("SELECT * FROM favoriteRecipes WHERE recipeId = :recipeId")
  suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe?
  // endregion

  // region UPSERT
  @Upsert
  suspend fun upsert(recipe: Recipe): Long

  @Upsert
  suspend fun upsert(chapter: Chapter): Long

  @Upsert
  suspend fun upsert(step: Step): Long

  @Upsert
  suspend fun upsert(ingredients: List<Ingredient>): List<Long>

  /**
   * Adds or updates the given [recipe] to the database
   * The items' order numbers will be changed according to the list indexing
   * @return Upserted recipes Id, whether the recipe was inserted or updated.
   */
  @Transaction
  suspend fun upsertRecipeWithChaptersStepsAndIngredients(recipe: RecipeWithChaptersStepsAndIngredients): Int {
    val existingRecipe = getRecipeWithChaptersStepsAndIngredients(recipe.recipe.id)

    // Delete old chapters. Steps and ingredients will be also deleted
    existingRecipe?.also {
      delete(*it.chapters.map { c -> c.chapter }.toTypedArray())
    }

    // Upsert function will return -1 if the function updates an existing item,
    //    so the value have to be set to the recipes id instead on the returned value
    val recipeId = upsert(recipe.recipe).toInt().let { if (it == -1) recipe.recipe.id else it }

    // Update chapter order numbers and ids
    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId =
        upsert(chapter.chapter.copy(orderNumber = ci + 1, recipeId = recipeId)).toInt()
          .let { if (it == -1) chapter.chapter.id else it }

      // Update step order numbers and ids
      chapter.steps.forEachIndexed { si, s ->
        s.letIf({ it.step.timerMinutes == 0 }) { it.copy(step = s.step.copy(timerMinutes = null)) }
          .also { step ->
            val stepId = upsert(step.step.copy(orderNumber = si + 1, chapterId = chapterId)).toInt()
              .let { if (it == -1) step.step.id else it }

            // Update ingredient ids
            upsert(step.ingredients.map { ingredient ->
              ingredient.copy(stepId = stepId)
            })
          }
      }
    }

    return recipeId
  }

  @Upsert
  suspend fun upsertRecipeRating(recipeRating: RecipeRating)
  // endregion

  // region DELETE
  @Delete
  suspend fun deleteRecipe(recipe: Recipe): Int

  @Delete
  suspend fun delete(vararg chapter: Chapter)

  @Delete
  suspend fun deleteStep(step: Step)

  @Delete
  suspend fun deleteIngredient(ingredient: Ingredient)

  @Delete
  suspend fun removeRecipeFromFavorites(value: RecipeBookmark)

  @Delete
  suspend fun deleteRecipeRating(recipeRating: RecipeRating)
  // endregion

  @Insert
  suspend fun addRecipeToFavorites(value: RecipeBookmark)
}