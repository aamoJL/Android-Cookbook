package com.aamo.cookbook.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.Step
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RecipeDatabaseTest {
  private lateinit var database: RecipeDatabase
  private lateinit var recipeDao: RecipeDao

  @Before
  fun setupDatabase() {
    database = Room.inMemoryDatabaseBuilder(
      context = ApplicationProvider.getApplicationContext(),
      klass = RecipeDatabase::class.java
    ).build()
    recipeDao = database.recipeDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDatabase() { database.close() }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_New() = runTest {
    var recipe = Recipe(name = "new recipe",  category = "new category")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }

    assert(recipe.id != 0)

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.value
    assertEquals(recipe, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_NewWithId() = runTest {
    var recipe = Recipe(id = 2, name = "new recipe",  category = "new category")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.value
    assertNotEquals(null, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_Existing() = runTest {
    var recipe = Recipe(name = "new recipe",  category = "new category")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it, name = "updated recipe", category = "updated category")
      recipeDao.upsertRecipe(recipe)
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.value
    assertEquals(recipe, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertChapter_New() = runTest {
    var recipe = Recipe(name = "new recipe",  category = "new category")
    var chapter = Chapter(name = "new chapter")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it)
    }

    assert(chapter.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.value
    assertEquals(chapter, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertChapter_Existing() = runTest {
    var recipe = Recipe(name = "new recipe",  category = "new category")
    var chapter = Chapter(name = "new chapter")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it, name = "updated name")
      recipeDao.upsertChapter(chapter)
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.value
    assertEquals(chapter, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertStep_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsertStep(step).toInt().also {
      step = step.copy(id = it)
    }

    assert(step.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.steps
        ?.firstOrNull { it.value.id == step.id }?.value
    assertEquals(step, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertStep_Existing() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsertStep(step).toInt().also {
      step = step.copy(id = it, description = "updated description")
      recipeDao.upsertStep(step)
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.steps
        ?.firstOrNull { it.value.id == step.id }?.value
    assertEquals(step, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertIngredient_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")
    var ingredient = Ingredient(name = "new ingredient")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsertStep(step).toInt().also {
      step = step.copy(id = it, chapterId = chapter.id)
      ingredient = ingredient.copy(stepId = step.id)
    }
    recipeDao.upsertIngredients(listOf(ingredient)).also {
      ingredient = ingredient.copy(id = it.first().toInt())
    }

    assert(ingredient.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.steps
        ?.firstOrNull { it.value.id == step.id }?.ingredients
        ?.firstOrNull { it.id == ingredient.id }
    assertEquals(ingredient, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertIngredient_Existing() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")
    var ingredient = Ingredient(name = "new ingredient")

    // Add items to the database and assign the produced id to the child items
    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsertChapter(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsertStep(step).toInt().also {
      step = step.copy(id = it, chapterId = chapter.id)
      ingredient = ingredient.copy(stepId = step.id)
    }
    recipeDao.upsertIngredients(listOf(ingredient)).also {
      ingredient = ingredient.copy(id = it.first().toInt(), name = "updated name")
      recipeDao.upsertIngredients(listOf(ingredient))
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters
        ?.firstOrNull { it.value.id == chapter.id }?.steps
        ?.firstOrNull { it.value.id == step.id }?.ingredients
        ?.firstOrNull { it.id == ingredient.id }
    assertEquals(ingredient, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_ItemsRemoved() = runTest {
    var recipe = Mocker.mockRecipeList().first()

    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe).also {
      // Assign id and remove the first chapter
      recipe = recipe.copy(
        value = recipe.value.copy(id = it),
        chapters = recipe.chapters.drop(1))
    }

    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe)

    // Order numbers should be updated
    val expected = recipe.copy(chapters = recipe.chapters.mapIndexed { index, chapter ->
      chapter.copy(value = chapter.value.copy(orderNumber = index + 1))
    })
    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.value.id)
    assertEquals(expected, actual)
  }

  @Test
  @Throws(IOException::class)
  fun deleteRecipe() = runTest {
    var recipe = Recipe(name = "new recipe",  category = "new category")

    recipeDao.upsertRecipe(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }
    recipeDao.deleteRecipe(recipe)

    assertNull(recipeDao.getRecipesFlow().first().firstOrNull { it.id == recipe.id })
  }

  @Test
  @Throws(IOException::class)
  fun getRecipes() = runTest {
    val count = 3
    repeat(count){
      recipeDao.upsertRecipe(Recipe(name = "name"))
    }

    val actual = recipeDao.getRecipesFlow().first().size
    assertEquals(count, actual)
  }

  @Test
  @Throws(IOException::class)
  fun getRecipeWithChaptersStepsAndIngredients() = runTest {
    val recipeList = Mocker.mockRecipeList()

    assertNotEquals(0, recipeList.size)

    recipeList.forEach { r ->
      recipeDao.upsertRecipe(r.value)
      r.chapters.forEach { c ->
        recipeDao.upsertChapter(c.value)
        c.steps.forEach { s ->
          recipeDao.upsertStep(s.value)
          recipeDao.upsertIngredients(s.ingredients)
        }
      }
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeList.first().value.id)
    assertEquals(recipeList.first(), actual)
  }
}