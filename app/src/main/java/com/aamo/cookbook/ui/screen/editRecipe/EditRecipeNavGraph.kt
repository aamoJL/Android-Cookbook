package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aamo.cookbook.Screen
import com.aamo.cookbook.utility.sharedViewModel
import com.aamo.cookbook.utility.toUUIDorNull
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.EditRecipeViewModel

/**
 * Enum class for screen navigation
 */
enum class EditRecipeScreenPage(val route: String) {
  EditRecipeInfo("edit/info"),
  EditRecipeChapter("edit/chapter"),
  EditChapterStep("edit/step"),
  EditStepIngredient("edit/ingredient")
}

fun NavGraphBuilder.editRecipeGraph(navHostController: NavHostController) {
  composable(EditRecipeScreenPage.EditRecipeInfo.route) {
    val parentEntry = remember(it) {
      navHostController.getBackStackEntry(Screen.EditRecipe.getRoute())
    }
    val recipeIdArgument =
      parentEntry.arguments?.getString(Screen.EditRecipe.argumentName)
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    LaunchedEffect(true) {
      val recipeId = recipeIdArgument?.toUUIDorNull()
      if (recipeId != null && editRecipeViewModel.infoUiState.value.id != recipeId) {
        val recipe =
          AppViewModel.Repositories.recipeRepository.getRecipe(recipeId)
        if (recipe != null)
          editRecipeViewModel.initInfoUiState(recipe)
      }
    }

    EditRecipeScreen(
      editRecipeViewModel,
      onEditChapter = { index ->
        editRecipeViewModel.initChapterUiState(
          chapter = editRecipeViewModel.infoUiState.value.chapters.elementAtOrNull(index),
          index = index
        )
        navHostController.navigate(EditRecipeScreenPage.EditRecipeChapter.route)
      },
      onSubmitChanges = {
        // TODO save recipe
      },
      onBack = { navHostController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditRecipeChapter.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterScreen(
      viewModel = editRecipeViewModel,
      onEditStep = { stepIndex ->
        editRecipeViewModel.initStepUiState(
          step = editRecipeViewModel.chapterUiState.value.steps.elementAtOrNull(stepIndex),
          index = stepIndex
        )
        navHostController.navigate(route = EditRecipeScreenPage.EditChapterStep.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateChapter(
          chapter = editRecipeViewModel.chapterUiState.value.toChapter(),
          index = editRecipeViewModel.chapterUiState.value.index
        )
        navHostController.navigateUp()
      },
      onBack = { navHostController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditChapterStep.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterStepScreen(
      viewModel = editRecipeViewModel,
      onEditIngredient = { ingredientIndex ->
        editRecipeViewModel.initIngredientUiState(
          ingredient = editRecipeViewModel.stepUiState.value.ingredients.elementAtOrNull(
            ingredientIndex
          ),
          index = ingredientIndex
        )
        navHostController.navigate(route = EditRecipeScreenPage.EditStepIngredient.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateStep(
          step = editRecipeViewModel.stepUiState.value.toStep(),
          index = editRecipeViewModel.stepUiState.value.index
        )
        navHostController.navigateUp()
      },
      onBack = { navHostController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditStepIngredient.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterStepIngredientScreen(
      viewModel = editRecipeViewModel,
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateIngredient(
          ingredient = editRecipeViewModel.ingredientUiState.value.toIngredient(),
          index = editRecipeViewModel.ingredientUiState.value.index
        )
        navHostController.navigateUp()
      },
      onBack = { navHostController.navigateUp() })
  }
}