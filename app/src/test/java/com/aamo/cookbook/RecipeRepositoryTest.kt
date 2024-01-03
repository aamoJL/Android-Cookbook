package com.aamo.cookbook

import com.aamo.cookbook.repository.RecipeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class RecipeRepositoryTest {

  @Test
  fun getRecipe() {
    // TODO: repo init
    val repo = RecipeRepository()
    val uuid = UUID.randomUUID()
    val recipe = repo.getRecipe(uuid)

    //assert(recipe != null)
  }

  @Test
  fun getRecipes_all() {
    val repo = RecipeRepository()
    val recipes = repo.getRecipes()

    assert(recipes.isNotEmpty())
  }

  @Test
  fun getRecipes_withCategory() {
    val repo = RecipeRepository()
    val category = "Jälkiruoka"
    val recipes = repo.getRecipes(category)

    assert(recipes.isNotEmpty() && recipes.all { it.category == category })
  }

  @Test
  fun addRecipe() {
    val repo = RecipeRepository()
    val recipe = Mocker.RecipeMocker().getExampleRecipe()

    repo.addRecipe(recipe)

    val repoRecipe = repo.getRecipe(recipe.id)

    assert(repoRecipe != null)
    assertEquals(repoRecipe, recipe)
  }
}