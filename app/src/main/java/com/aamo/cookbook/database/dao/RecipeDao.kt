@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
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
  suspend fun getRecipe(recipeId: Long): Recipe?

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

  @Transaction
  @Query(
    """
    SELECT * FROM recipes
    LEFT JOIN recipeChapters AS chapters ON chapters.recipeId = recipes.id
    LEFT JOIN chapterSteps AS steps ON steps.chapterId = chapters.id
    LEFT JOIN ingredients ON ingredients.stepId = steps.id
    WHERE recipes.id = :recipeId
    ORDER BY chapters.orderNumber, steps.orderNumber, ingredients.name
  """
  )
  suspend fun getCompleteRecipe(recipeId: Long): RecipeWithChaptersStepsAndIngredients?

  @Transaction
  @Query(
    """
    SELECT * FROM recipes
    LEFT JOIN recipeChapters AS chapters ON chapters.recipeId = recipes.id
    LEFT JOIN chapterSteps AS steps ON steps.chapterId = chapters.id
    LEFT JOIN ingredients ON ingredients.stepId = steps.id
    WHERE recipes.id = :recipeId
    ORDER BY chapters.orderNumber, steps.orderNumber, ingredients.name
  """
  )
  fun getCompleteRecipeFlow(recipeId: Long): Flow<RecipeWithChaptersStepsAndIngredients?>

  @Query("SELECT * FROM recipeRatings WHERE recipeId = :recipeId")
  fun getRatingFlow(recipeId: Long): Flow<RecipeRating?>

  @Query("SELECT * FROM favoriteRecipes WHERE recipeId = :recipeId")
  fun getBookmarkFlow(recipeId: Long): Flow<RecipeBookmark?>
  // endregion

  // region UPSERT
  @Upsert
  suspend fun upsert(recipe: Recipe): Long

  @Upsert
  suspend fun upsert(chapter: Chapter): Long

  @Upsert
  suspend fun upsert(step: Step): Long

  @Upsert
  suspend fun upsert(vararg ingredients: Ingredient)

  /**
   * Adds or updates the given [recipe] to the database
   * The items' order numbers will be changed according to the list indexing
   * @return Recipe's Id, whether the recipe was inserted or updated.
   */
  @Transaction
  suspend fun upsert(recipe: RecipeWithChaptersStepsAndIngredients): Long {
    val existingRecipe = getCompleteRecipe(recipe.recipe.id)

    // Delete old chapters. Steps and ingredients will be also deleted
    existingRecipe?.also {
      delete(*it.chapters.map { c -> c.chapter }.toTypedArray())
    }

    // Upsert function will return -1 if the function updates an existing item,
    //    so the value have to be set to the recipes id instead on the returned value
    val recipeId = upsert(recipe.recipe).let { if (it == -1L) recipe.recipe.id else it }

    // Update chapter order numbers and ids
    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId = upsert(
        chapter.chapter.copy(orderNumber = ci + 1, recipeId = recipeId)
      ).let { if (it == -1L) chapter.chapter.id else it }

      // Update step order numbers and ids
      chapter.steps.forEachIndexed { si, s ->
        s.letIf({ it.step.timerMinutes == 0 }) { it.copy(step = s.step.copy(timerMinutes = null)) }
          .also { step ->
            val stepId = upsert(
              step.step.copy(orderNumber = si + 1, chapterId = chapterId)
            ).let { if (it == -1L) step.step.id else it }

            // Update ingredient ids
            upsert(*step.ingredients.map { ingredient -> ingredient.copy(stepId = stepId) }
              .toTypedArray())
          }
      }
    }

    return recipeId
  }

  @Upsert
  suspend fun upsert(recipeRating: RecipeRating): Long

  @Upsert
  suspend fun upsert(recipeBookmark: RecipeBookmark): Long
  // endregion

  // region DELETE
  @Delete
  suspend fun delete(recipe: Recipe): Int

  @Delete
  suspend fun delete(vararg chapter: Chapter): Int

  @Delete
  suspend fun delete(bookmark: RecipeBookmark): Int

  @Delete
  suspend fun delete(rating: RecipeRating): Int
  // endregion
}