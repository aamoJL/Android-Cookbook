package com.aamo.cookbook.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
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

  @Transaction
  suspend fun upsertRecipeWithChaptersStepsAndIngredients(recipe: RecipeWithChaptersStepsAndIngredients) : Int {
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

  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipes(): Flow<List<Recipe>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE id = :recipeId")
  suspend fun getRecipeWithChaptersStepsAndIngredients(recipeId: Int): RecipeWithChaptersStepsAndIngredients?
}