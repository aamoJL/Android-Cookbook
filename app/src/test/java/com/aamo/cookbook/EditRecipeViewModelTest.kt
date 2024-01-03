package com.aamo.cookbook

import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class EditRecipeViewModelTest {

  @Test
  fun initInfoUiState() {
    val vm = EditRecipeViewModel()
    val recipe = Mocker.RecipeMocker().getExampleRecipe()

    vm.initInfoUiState(recipe)

    assertEquals(
      vm.infoUiState.value, EditRecipeViewModel.InfoScreenUiState(
        id = recipe.id,
        name = recipe.name,
        category = recipe.category,
        subCategory = recipe.subCategory,
        servings = recipe.servings,
        chapters = recipe.chapters,
        unsavedChanges = false
      )
    )
  }

  @Test
  fun initChapterUiState() {
    val vm = EditRecipeViewModel()
    val index = 0
    val chapter = Mocker.RecipeMocker().getExampleRecipe().chapters.elementAt(index)

    vm.initChapterUiState(chapter, index)

    assertEquals(
      vm.chapterUiState.value, EditRecipeViewModel.ChapterScreenUiState(
        index = index,
        id = chapter.id,
        name = chapter.name,
        steps = chapter.steps,
        unsavedChanges = false
      )
    )
  }

  @Test
  fun initStepUiState() {
    val vm = EditRecipeViewModel()
    val index = 0
    val step = Mocker.RecipeMocker().getExampleRecipe().chapters.elementAt(index)
      .steps.elementAt(index)

    vm.initStepUiState(step, index)

    assertEquals(
      vm.stepUiState.value, EditRecipeViewModel.StepScreenUiState(
        index = index,
        id = step.id,
        description = step.description,
        ingredients = step.ingredients,
        unsavedChanges = false
      )
    )
  }

  @Test
  fun initIngredientUiState() {
    val vm = EditRecipeViewModel()
    val index = 0
    val ingredient = Mocker.RecipeMocker().getExampleRecipe().chapters.elementAt(index)
      .steps.elementAt(index).ingredients.elementAt(index)

    vm.initIngredientUiState(ingredient, index)

    assertEquals(
      vm.ingredientUiState.value, EditRecipeViewModel.IngredientScreenUiState(
        index = index,
        id = ingredient.id,
        name = ingredient.name,
        unit = ingredient.unit,
        amount = ingredient.amount,
        unsavedChanges = false
      )
    )
  }

  @Test
  fun addOrUpdateChapter_add() {
    val vm = EditRecipeViewModel()
    val oldUiState = vm.infoUiState.value
    val chapter = Recipe.Chapter(name = "new")

    vm.addOrUpdateChapter(chapter, -1)

    assertEquals(
      vm.infoUiState.value, oldUiState.copy(
        chapters = oldUiState.chapters.plus(chapter),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun addOrUpdateChapter_update() {
    val vm = EditRecipeViewModel()

    vm.initInfoUiState(Mocker.RecipeMocker().getExampleRecipe())

    val oldUiState = vm.infoUiState.value
    val chapter = Recipe.Chapter(name = "updated")
    val index = 0

    vm.addOrUpdateChapter(chapter, index)

    assertEquals(
      vm.infoUiState.value, oldUiState.copy(
        chapters = oldUiState.chapters.toMutableSet().mapIndexed { i, c ->
          if (i == index) chapter else c
        }.toSet(),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun addOrUpdateStep_add() {
    val vm = EditRecipeViewModel()
    val oldUiState = vm.chapterUiState.value
    val step = Recipe.Chapter.Step("new")

    vm.addOrUpdateStep(step, -1)

    assertEquals(
      vm.chapterUiState.value, oldUiState.copy(
        steps = oldUiState.steps.plus(step),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun addOrUpdateStep_update() {
    val vm = EditRecipeViewModel()

    vm.initChapterUiState(Mocker.RecipeMocker().getExampleRecipe().chapters.elementAt(0), 0)

    val oldUiState = vm.chapterUiState.value
    val step = Recipe.Chapter.Step("updated")
    val index = 0

    vm.addOrUpdateStep(step, index)

    assertEquals(
      vm.chapterUiState.value, oldUiState.copy(
        steps = oldUiState.steps.toMutableSet().mapIndexed { i, s ->
          if (i == index) step else s
        }.toSet(),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun addOrUpdateIngredient_add() {
    val vm = EditRecipeViewModel()
    val oldUiState = vm.stepUiState.value
    val ingredient = Recipe.Ingredient("new", 1f, "unit")

    vm.addOrUpdateIngredient(ingredient, -1)

    assertEquals(
      vm.stepUiState.value, oldUiState.copy(
        ingredients = oldUiState.ingredients.plus(ingredient),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun addOrUpdateIngredient_update() {
    val vm = EditRecipeViewModel()

    vm.initStepUiState(
      Mocker.RecipeMocker().getExampleRecipe().chapters.elementAt(0).steps.elementAt(0), 0
    )

    val oldUiState = vm.stepUiState.value
    val ingredient = Recipe.Ingredient("updated", 2f, "unit")
    val index = 0

    vm.addOrUpdateIngredient(ingredient, index)

    assertEquals(
      vm.stepUiState.value, oldUiState.copy(
        ingredients = oldUiState.ingredients.toMutableSet().mapIndexed { i, ingr ->
          if (i == index) ingredient else ingr
        }.toSet(),
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setRecipeName() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setRecipeName(value)

    assertEquals(
      vm.infoUiState.value, vm.infoUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setCategory() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setCategory(value)

    assertEquals(
      vm.infoUiState.value, vm.infoUiState.value.copy(
        category = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setSubCategory() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setSubCategory(value)

    assertEquals(
      vm.infoUiState.value, vm.infoUiState.value.copy(
        subCategory = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setServings() {
    val vm = EditRecipeViewModel()
    val value = 10
    vm.setServings(value)

    assertEquals(
      vm.infoUiState.value, vm.infoUiState.value.copy(
        servings = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setChapterName() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setChapterName(value)

    assertEquals(
      vm.chapterUiState.value, vm.chapterUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setStepDescription() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setStepDescription(value)

    assertEquals(
      vm.stepUiState.value, vm.stepUiState.value.copy(
        description = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientName() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setIngredientName(value)

    assertEquals(
      vm.ingredientUiState.value, vm.ingredientUiState.value.copy(
        name = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientUnit() {
    val vm = EditRecipeViewModel()
    val value = "new"
    vm.setIngredientUnit(value)

    assertEquals(
      vm.ingredientUiState.value, vm.ingredientUiState.value.copy(
        unit = value,
        unsavedChanges = true
      )
    )
  }

  @Test
  fun setIngredientAmount() {
    val vm = EditRecipeViewModel()
    val value = 10f
    vm.setIngredientAmount(value)

    assertEquals(
      vm.ingredientUiState.value, vm.ingredientUiState.value.copy(
        amount = value,
        unsavedChanges = true
      )
    )
  }
}