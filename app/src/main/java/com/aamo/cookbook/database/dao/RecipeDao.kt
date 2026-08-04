@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.RoomWarnings
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
  @Query("SELECt * FROM recipe WHERE id = :recipeId")
  suspend fun getRecipe(recipeId: Long): Recipe?

  @Query("SELECT DISTINCT category FROM recipe")
  fun getCategoriesFlow(): Flow<List<String>>

  @Query(
    """
    SELECT DISTINCT category, subCategory FROM recipe
    WHERE NOT category = ''
    ORDER BY category
  """
  )
  suspend fun getCategoriesMap(): Map<@MapColumn(columnName = "category") String, List<@MapColumn(
    columnName = "subCategory"
  ) String>>

  @Transaction
  @Query("SELECT * FROM recipe ORDER BY name ASC")
  fun getRecipesWithBookmarkAndRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>>

  @Transaction
  @Query("SELECT * FROM recipe WHERE category = :category ORDER BY name ASC")
  fun getRecipesWithBookmarkAndRatingFlow(category: String): Flow<List<RecipeWithBookmarkAndRating>>

  @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
  @Transaction
  @Query(
    """
    SELECT recipe.*, bookmark.*, rating.* FROM recipe
    JOIN recipeBookmark AS bookmark ON bookmark.recipeId = recipe.id
    LEFT OUTER JOIN recipeRating AS rating ON rating.recipeId = recipe.id
  """
  )
  fun getBookmarksWithRatingFlow(): Flow<List<RecipeWithBookmarkAndRating>>

  @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
  @Transaction
  @Query(
    """
    SELECT recipe.*, chapter.*, step.*, ingredient.* FROM recipe
    LEFT JOIN recipeChapter AS chapter ON chapter.recipeId = recipe.id
    LEFT JOIN chapterStep AS step ON step.chapterId = chapter.id
    LEFT JOIN ingredient ON ingredient.stepId = step.id
    WHERE recipe.id = :recipeId
    ORDER BY chapter.orderNumber, step.orderNumber, ingredient.name
  """
  )
  suspend fun getCompleteRecipe(recipeId: Long): RecipeWithChaptersStepsAndIngredients?

  @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
  @Transaction
  @Query(
    """
    SELECT recipe.*, chapter.*, step.*, ingredient.* FROM recipe
    LEFT JOIN recipeChapter AS chapter ON chapter.recipeId = recipe.id
    LEFT JOIN chapterStep AS step ON step.chapterId = chapter.id
    LEFT JOIN ingredient ON ingredient.stepId = step.id
    ORDER BY chapter.orderNumber, step.orderNumber, ingredient.name
  """
  )
  suspend fun getCompleteRecipes(): List<RecipeWithChaptersStepsAndIngredients>

  @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
  @Transaction
  @Query(
    """
    SELECT recipe.*, chapter.*, step.*, ingredient.* FROM recipe
    LEFT JOIN recipeChapter AS chapter ON chapter.recipeId = recipe.id
    LEFT JOIN chapterStep AS step ON step.chapterId = chapter.id
    LEFT JOIN ingredient ON ingredient.stepId = step.id
    WHERE recipe.id = :recipeId
    ORDER BY chapter.orderNumber, step.orderNumber, ingredient.name
  """
  )
  fun getCompleteRecipeFlow(recipeId: Long): Flow<RecipeWithChaptersStepsAndIngredients?>

  @Query("SELECT * FROM recipeRating WHERE recipeId = :recipeId")
  fun getRatingFlow(recipeId: Long): Flow<RecipeRating?>

  @Query("SELECT * FROM recipeBookmark WHERE recipeId = :recipeId")
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
   * @return Recipe's id, whether the recipe was inserted or updated.
   */
  @Transaction
  suspend fun upsert(recipe: RecipeWithChaptersStepsAndIngredients): Long {
    val dbRecipe = getCompleteRecipe(recipe.recipe.id)

    // Upsert recipe
    // Upsert function will return -1 if the function updates an existing item,
    //    so the value have to be set to the recipes id instead on the returned value
    val recipeId = upsert(recipe.recipe).let { if (it == -1L) recipe.recipe.id else it }

    // Delete deleted chapters.
    val dbChapters = dbRecipe?.chapters ?: emptyList()
    val chapters = recipe.chapters
    dbChapters.filter { dbC -> chapters.firstOrNull { it.chapter.id == dbC.chapter.id } == null }
      .also { deletedChapters ->
        if (deletedChapters.isNotEmpty()) {
          delete(*deletedChapters.map { it.chapter }.toTypedArray())
          Log.d("debug", "deleted chapters: ${deletedChapters.map { it.chapter.id }}")
        }
      }

    // Delete deleted steps. Ingredients will also be deleted
    val dbSteps = dbChapters.flatMap { it.steps }
    val steps = recipe.chapters.flatMap { c -> c.steps }
    dbSteps.filter { dbS -> steps.firstOrNull { it.step.id == dbS.step.id } == null }
      .also { deletedSteps ->
        if (deletedSteps.isNotEmpty()) {
          delete(*deletedSteps.map { it.step }.toTypedArray())
          Log.d("debug", "deleted steps: ${deletedSteps.map { it.step.id }}")
        }
      }

    // Delete deleted Ingredients
    val dbIngredients = dbSteps.flatMap { it.ingredients }
    val ingredients = steps.flatMap { it.ingredients }
    dbIngredients.filter { dbI -> ingredients.firstOrNull { it.id == dbI.id } == null }
      .also { deletedIngredients ->
        if (deletedIngredients.isNotEmpty()) {
          delete(*deletedIngredients.toTypedArray())
          Log.d("debug", "deleted ingredients: ${deletedIngredients.map { it.id }}")
        }
      }

    // Update chapter properties
    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId = upsert(
        chapter.chapter.copy(orderNumber = ci + 1, recipeId = recipeId)
      ).let { if (it == -1L) chapter.chapter.id else it }

      // Update step properties
      chapter.steps.forEachIndexed { si, s ->
        s.letIf({ it.step.timerMinutes == 0 }) { it.copy(step = s.step.copy(timerMinutes = null)) }
          .also { step ->
            val stepId = upsert(
              step.step.copy(orderNumber = si + 1, chapterId = chapterId)
            ).let { if (it == -1L) step.step.id else it }

            // Update ingredient properties
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
  suspend fun delete(vararg step: Step): Int

  @Delete
  suspend fun delete(vararg ingredient: Ingredient): Int

  @Delete
  suspend fun delete(bookmark: RecipeBookmark): Int

  @Delete
  suspend fun delete(rating: RecipeRating): Int
  // endregion
}