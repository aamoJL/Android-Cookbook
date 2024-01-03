package com.aamo.cookbook

import com.aamo.cookbook.viewModel.AppViewModel
import org.junit.jupiter.api.Test
import java.util.UUID

// TODO: repository injection
class AppViewModelTest {

  @Test
  fun getCategories() {
    val vm = AppViewModel()
    val categories = vm.getCategories()

    assert(categories.distinctBy { it }.size == categories.size)
  }

  @Test
  fun getRecipes() {
    val vm = AppViewModel()
    val category = "Jälkiruoka"
    val recipes = vm.getRecipes(category)

    assert(recipes.all { it.category == category })
  }

  @Test
  fun getRecipe() {
    val vm = AppViewModel()
    // TODO: get recipe test
    val id = UUID.randomUUID()
    val recipe = vm.getRecipe(id)
  }
}