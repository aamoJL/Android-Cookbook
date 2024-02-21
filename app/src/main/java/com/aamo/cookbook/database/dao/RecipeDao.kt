package com.aamo.cookbook.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.FullFavoriteRecipe
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.model.RecipeWithFavoriteAndRating
import com.aamo.cookbook.model.Step
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
  @Upsert
  suspend fun upsertRecipe(recipe: Recipe) : Long

  @Upsert
  suspend fun upsertChapter(chapter: Chapter) : Long

  @Upsert
  suspend fun upsertStep(step: Step) : Long

  @Upsert
  suspend fun upsertIngredients(ingredients: List<Ingredient>) : List<Long>

  /**
   * Adds or updates the given [recipe] to the database
   * The items' order numbers will be changed to according to the list indexing
   * @return Upserted recipes Id, whether the recipe was added or updated.
   */
  @Transaction
  suspend fun upsertRecipeWithChaptersStepsAndIngredients(recipe: RecipeWithChaptersStepsAndIngredients) : Int {
    // Delete items that are not in the recipe anymore
    getRecipeWithChaptersStepsAndIngredients(recipe.value.id).also { oldRecipe ->
      if (oldRecipe != null) {
        val currentChapterIds = recipe.chapters.map { c -> c.value.id }.filter { it != 0 }
        val currentStepIds =
          recipe.chapters.flatMap { c -> c.steps.map { s -> s.value.id }.filter { it != 0 } }
        val currentIngredientIds = recipe.chapters.flatMap { c ->
          c.steps.flatMap { s -> s.ingredients.map { i -> i.id } }.filter { it != 0 }
        }

        oldRecipe.chapters.forEach { chapter ->
          if (!currentChapterIds.contains(chapter.value.id)) deleteChapter(chapter.value)
          else {
            chapter.steps.forEach { step ->
              if (!currentStepIds.contains(step.value.id)) deleteStep(step.value)
              else {
                step.ingredients.forEach { ingredient ->
                  if (!currentIngredientIds.contains(ingredient.id)) deleteIngredient(ingredient)
                }
              }
            }
          }
        }
      }
    }

    // Upsert function will return -1 if the function updates an existing item,
    // so the value have to be set to the recipes id instead on the returned value
    val recipeId = upsertRecipe(recipe.value).toInt()
      .let { if (it == -1) recipe.value.id else it }

    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId =
        upsertChapter(chapter.value.copy(orderNumber = ci + 1, recipeId = recipeId))
          .toInt()
          .let { if (it == -1) chapter.value.id else it }

      chapter.steps.forEachIndexed { si, s ->
        val step =
          if (s.value.timerMinutes == 0) s.copy(value = s.value.copy(timerMinutes = null)) else s

        val stepId =
          upsertStep(step.value.copy(orderNumber = si + 1, chapterId = chapterId))
            .toInt()
            .let { if (it == -1) step.value.id else it }

        upsertIngredients(step.ingredients.map { ingredient ->
          ingredient.copy(stepId = stepId)
        })
      }
    }

    return recipeId
  }

  @Delete
  suspend fun deleteRecipe(recipe: Recipe)

  @Delete
  suspend fun deleteChapter(chapter: Chapter)

  @Delete
  suspend fun deleteStep(step: Step)

  @Delete
  suspend fun deleteIngredient(ingredient: Ingredient)

  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipesFlow(): Flow<List<Recipe>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE id = :recipeId")
  @Deprecated(
    message = "This function returns unsorted recipe",
    replaceWith = ReplaceWith("getRecipeWithChaptersStepsAndIngredients(recipeId)"))
  suspend fun getRecipeWithChaptersStepsAndIngredientsUnsorted(recipeId: Int): RecipeWithChaptersStepsAndIngredients?

  /**
   * Returns recipes with chapters and steps sorted by order number, and ingredients sorted by with name
   */
  suspend fun getRecipeWithChaptersStepsAndIngredients(recipeId: Int): RecipeWithChaptersStepsAndIngredients? {
    @Suppress("DEPRECATION")
    return getRecipeWithChaptersStepsAndIngredientsUnsorted(recipeId)?.let { recipe ->
      recipe.copy(
        chapters = recipe.chapters.sortedBy { it.value.orderNumber }.map { chapter ->
          chapter.copy(
            steps = chapter.steps.sortedBy { it.value.orderNumber }.map { step ->
              step.copy(
                ingredients = step.ingredients.sortedBy { it.name }
              )
            }
          )
        }
      )
    }
  }

  @Query("SELECT DISTINCT category, subCategory FROM recipes")
  suspend fun getCategoriesWithSubcategories(): List<RecipeCategoryTuple>

  @Query("SELECT * FROM recipeRatings WHERE recipeId = :recipeId")
  suspend fun getRecipeRatingById(recipeId: Int): RecipeRating?

  @Insert
  suspend fun addRecipeToFavorites(value: FavoriteRecipe)

  @Delete
  suspend fun removeRecipeFromFavorites(value: FavoriteRecipe)

  @Transaction
  @Query("SELECT * FROM favoriteRecipes WHERE recipeId = :recipeId")
  suspend fun getFavoriteRecipeById(recipeId: Int): FullFavoriteRecipe?

  @Transaction
  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipesWithFavoriteAndRatingFlow(): Flow<List<RecipeWithFavoriteAndRating>>

  @Upsert
  suspend fun upsertRecipeRating(recipeRating: RecipeRating)

  @Delete
  suspend fun deleteRecipeRating(recipeRating: RecipeRating)
}