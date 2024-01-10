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

  @Delete
  suspend fun deleteRecipe(recipe: Recipe)

  @Query("SELECT * FROM recipes ORDER BY name ASC")
  fun getRecipes(): Flow<List<Recipe>>

  @Transaction
  @Query("SELECT * FROM recipes WHERE id = :recipeId")
  suspend fun getRecipeWithChaptersStepsAndIngredients(recipeId: Int): RecipeWithChaptersStepsAndIngredients?
}