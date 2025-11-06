package com.aamo.cookbook.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.utility.extensions.swap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
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
      context = ApplicationProvider.getApplicationContext(), klass = RecipeDatabase::class.java
    ).build()
    recipeDao = database.recipeDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDatabase() {
    database.close()
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }

    assert(recipe.id != 0)

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.recipe
    assertEquals(recipe, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_NewWithId() = runTest {
    var recipe = Recipe(id = 2, name = "new recipe", category = "new category")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.recipe
    assertNotEquals(null, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_Existing() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it, name = "updated recipe", category = "updated category")
      recipeDao.upsert(recipe)
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.recipe
    assertEquals(recipe, actual)
  }

  @Test
  fun upsertRecipeWithChaptersStepsAndIngredients_New() = runTest {
    val newRecipe = Mocker.mockRecipeList().first().copyAsNew()
    val recipeId = recipeDao.upsertRecipeWithChaptersStepsAndIngredients(newRecipe)

    recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeId)?.also { actual ->
      assertEquals(newRecipe.recipe.copy(id = recipeId), actual.recipe)
      assert(actual.chapters.isNotEmpty())

      actual.chapters.forEachIndexed { ci, chapter ->
        assertEquals(
          newRecipe.chapters[ci].chapter.copy(
            id = assertNotEquals(0, chapter.chapter.id).let { chapter.chapter.id },
            recipeId = recipeId,
            orderNumber = ci + 1
          ), chapter.chapter
        )
        assert(chapter.steps.isNotEmpty())

        chapter.steps.forEachIndexed { si, step ->
          assertEquals(
            newRecipe.chapters[ci].steps[si].step.copy(
              id = assertNotEquals(0, step.step.id).let { step.step.id },
              chapterId = chapter.chapter.id,
              orderNumber = si + 1
            ), step.step
          )
          assert(step.ingredients.isNotEmpty())

          step.ingredients.forEachIndexed { ii, ingredient ->
            assertEquals(
              newRecipe.chapters[ci].steps[si].ingredients[ii].copy(
                id = assertNotEquals(0, ingredient.id).let { ingredient.id }, stepId = step.step.id
              ), ingredient
            )
          }
        }
      }
    } ?: fail("Recipe was not found")
  }

  @Test
  fun upsertRecipeWithChaptersStepsAndIngredients_Existing() = runTest {
    val recipeId = recipeDao.upsertRecipeWithChaptersStepsAndIngredients(
      Mocker.mockRecipeList().first().copyAsNew()
    )

    recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeId)?.also { existing ->
      val expected = existing.copy(
        recipe = existing.recipe.copy(
          name = "Updated name"
        )
      )

      recipeDao.upsertRecipeWithChaptersStepsAndIngredients(expected)
      val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeId)

      assertEquals(expected, actual)
    } ?: fail("Recipe was not found")
  }

  @Test
  fun upsertRecipeWithChaptersStepsAndIngredients_Existing_Reordered() = runTest {
    val recipeId = recipeDao.upsertRecipeWithChaptersStepsAndIngredients(
      Mocker.mockRecipeList().first().copyAsNew()
    )

    recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeId)?.also { existing ->
      val swappedRecipe = existing.copy(
        chapters = existing.chapters.toMutableList().apply {
          this.swap(0, 1)
        })
      assertEquals(1, swappedRecipe.chapters[1].chapter.orderNumber)
      assertEquals(2, swappedRecipe.chapters[0].chapter.orderNumber)

      recipeDao.upsertRecipeWithChaptersStepsAndIngredients(swappedRecipe)

      val expected = swappedRecipe.copy(
        chapters = swappedRecipe.chapters.mapIndexed { index, chapter ->
          chapter.copy(
            chapter = chapter.chapter.copy(orderNumber = index + 1)
          )
        })

      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeId)?.also { actual ->
        assertEquals(expected, actual)
      } ?: fail("Recipe was not found")
    } ?: fail("Recipe was not found")
  }

  @Test
  @Throws(IOException::class)
  fun upsertChapter_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it)
    }

    assert(chapter.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.chapter
    assertEquals(chapter, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertChapter_Existing() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it, name = "updated name")
      recipeDao.upsert(chapter)
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.chapter
    assertEquals(chapter, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertStep_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsert(step).toInt().also {
      step = step.copy(id = it)
    }

    assert(step.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.steps?.firstOrNull { it.step.id == step.id }?.step
    assertEquals(step, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertStep_Existing() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsert(step).toInt().also {
      step = step.copy(id = it, description = "updated description")
      recipeDao.upsert(step)
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.steps?.firstOrNull { it.step.id == step.id }?.step
    assertEquals(step, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertIngredient_New() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")
    var chapter = Chapter(name = "new chapter")
    var step = Step(description = "new step")
    var ingredient = Ingredient(name = "new ingredient")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsert(step).toInt().also {
      step = step.copy(id = it, chapterId = chapter.id)
      ingredient = ingredient.copy(stepId = step.id)
    }
    recipeDao.upsert(listOf(ingredient)).also {
      ingredient = ingredient.copy(id = it.first().toInt())
    }

    assert(ingredient.id != 0)

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.steps?.firstOrNull { it.step.id == step.id }?.ingredients?.firstOrNull { it.id == ingredient.id }
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
    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
      chapter = chapter.copy(recipeId = it)
    }
    recipeDao.upsert(chapter).toInt().also {
      chapter = chapter.copy(id = it)
      step = step.copy(chapterId = it)
    }
    recipeDao.upsert(step).toInt().also {
      step = step.copy(id = it, chapterId = chapter.id)
      ingredient = ingredient.copy(stepId = step.id)
    }
    recipeDao.upsert(listOf(ingredient)).also {
      ingredient = ingredient.copy(id = it.first().toInt(), name = "updated name")
      recipeDao.upsert(listOf(ingredient))
    }

    val actual =
      recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.id)?.chapters?.firstOrNull { it.chapter.id == chapter.id }?.steps?.firstOrNull { it.step.id == step.id }?.ingredients?.firstOrNull { it.id == ingredient.id }
    assertEquals(ingredient, actual)
  }

  @Test
  @Throws(IOException::class)
  fun upsertRecipe_ItemsRemoved() = runTest {
    var recipe = Mocker.mockRecipeList().first()

    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe).also {
      // Assign id and remove the first chapter
      recipe = recipe.copy(
        recipe = recipe.recipe.copy(id = it), chapters = recipe.chapters.drop(1)
      )
    }

    recipeDao.upsertRecipeWithChaptersStepsAndIngredients(recipe)

    // Order numbers should be updated
    val expected = recipe.copy(chapters = recipe.chapters.mapIndexed { index, chapter ->
      chapter.copy(chapter = chapter.chapter.copy(orderNumber = index + 1))
    })
    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipe.recipe.id)
    assertEquals(expected, actual)
  }

  @Test
  @Throws(IOException::class)
  fun deleteRecipe() = runTest {
    var recipe = Recipe(name = "new recipe", category = "new category")

    recipeDao.upsert(recipe).toInt().also {
      recipe = recipe.copy(id = it)
    }
    recipeDao.deleteRecipe(recipe)

    assertNull(recipeDao.getRecipesFlow().first().firstOrNull { it.id == recipe.id })
  }

  @Test
  @Throws(IOException::class)
  fun getRecipes() = runTest {
    val count = 3
    repeat(count) {
      recipeDao.upsert(Recipe(name = "name"))
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
      recipeDao.upsert(r.recipe)
      r.chapters.forEach { c ->
        recipeDao.upsert(c.chapter)
        c.steps.forEach { s ->
          recipeDao.upsert(s.step)
          recipeDao.upsert(s.ingredients)
        }
      }
    }

    val actual = recipeDao.getRecipeWithChaptersStepsAndIngredients(recipeList.first().recipe.id)
    assertEquals(recipeList.first(), actual)
  }
}