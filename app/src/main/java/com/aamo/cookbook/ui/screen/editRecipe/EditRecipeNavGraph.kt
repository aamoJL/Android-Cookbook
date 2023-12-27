package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aamo.cookbook.AppViewModel
import com.aamo.cookbook.Screen
import com.aamo.cookbook.utility.sharedViewModel
import com.aamo.cookbook.utility.toUUIDorNull

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
      navHostController.getBackStackEntry(Screen.EditRecipe.route)
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

    EditRecipeScreen().Screen(
      editRecipeViewModel,
      onEditChapter = { index ->
        editRecipeViewModel.selectedChapterIndex = index
        navHostController.navigate(EditRecipeScreenPage.EditRecipeChapter.route)
      },
      onSubmitChanges = {
        // TODO save recipe
      })
  }
  composable(EditRecipeScreenPage.EditRecipeChapter.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterScreen().Screen(
      viewModel = editRecipeViewModel,
      onEditStep = { stepIndex ->
        editRecipeViewModel.selectedStepIndex = stepIndex
        navHostController.navigate(route = EditRecipeScreenPage.EditChapterStep.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.applyChapterChanges()
        navHostController.navigateUp()
      }
    )
  }
  composable(EditRecipeScreenPage.EditChapterStep.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterStepScreen().Screen(
      viewModel = editRecipeViewModel,
      onEditIngredient = { ingredientIndex ->
        editRecipeViewModel.selectedIngredientIndex = ingredientIndex
        navHostController.navigate(route = EditRecipeScreenPage.EditStepIngredient.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.applyStepChanges()
        navHostController.navigateUp()
      }
    )
  }
  composable(EditRecipeScreenPage.EditStepIngredient.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navHostController)

    EditRecipeChapterStepIngredientScreen().Screen(
      viewModel = editRecipeViewModel,
      onSubmitChanges = {
        editRecipeViewModel.applyIngredientChanges()
        navHostController.navigateUp()
      })
  }
}