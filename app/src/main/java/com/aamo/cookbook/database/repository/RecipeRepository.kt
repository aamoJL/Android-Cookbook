package com.aamo.cookbook.database.repository

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
  fun getAllRecipesStream(): Flow<List<Recipe>>
  suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients?
  suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int
  suspend fun deleteRecipe(recipe: Recipe)
}

class OfflineRecipeRepository(private val recipeDao: RecipeDao) : RecipeRepository {
  override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getRecipes()

  override suspend fun getRecipeWithChaptersStepsAndIngredients(id: Int): RecipeWithChaptersStepsAndIngredients? =
    recipeDao.getRecipeWithChaptersStepsAndIngredients(id)

  override suspend fun upsertRecipe(recipe: RecipeWithChaptersStepsAndIngredients) : Int {
    val recipeId = recipeDao.upsertRecipe(recipe.value).toInt()
      .let { if (it == -1) recipe.value.id else it }

    recipe.chapters.forEachIndexed { ci, chapter ->
      val chapterId =
        recipeDao.upsertChapter(chapter.value.copy(orderNumber = ci + 1, recipeId = recipeId))
          .toInt()
          .let { if (it == -1) chapter.value.id else it }

      chapter.steps.forEachIndexed { si, step ->
        val stepId =
          recipeDao.upsertStep(step.value.copy(orderNumber = si + 1, chapterId = chapterId))
            .toInt()
            .let { if (it == -1) step.value.id else it }

        recipeDao.upsertIngredients(step.ingredients.map { ingredient ->
          ingredient.copy(stepId = stepId)
        })
      }
    }

    return recipeId
  }

  override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
}