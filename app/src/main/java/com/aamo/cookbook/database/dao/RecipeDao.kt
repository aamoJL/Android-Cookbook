package com.aamo.cookbook.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategoryTuple
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
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

    val recipeId = upsertRecipe(recipe.value).toInt()
      .let { if (it == -1) recipe.value.id else it }

    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId =
        upsertChapter(chapter.value.copy(orderNumber = ci + 1, recipeId = recipeId))
          .toInt()
          .let { if (it == -1) chapter.value.id else it }

      chapter.steps.forEachIndexed { si, step ->
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
  fun getRecipes(): Flow<List<Recipe>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE id = :recipeId")
  suspend fun getRecipeWithChaptersStepsAndIngredients(recipeId: Int): RecipeWithChaptersStepsAndIngredients?

  @Query("SELECT DISTINCT category FROM recipes ")
  suspend fun getCategories(): List<String>

  @Query("SELECT category, subCategory FROM recipes")
  suspend fun getCategoriesWithSubcategories(): List<RecipeCategoryTuple>
}