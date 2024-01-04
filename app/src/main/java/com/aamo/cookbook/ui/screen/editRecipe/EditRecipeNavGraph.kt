package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.aamo.cookbook.Screen
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.utility.sharedViewModel
import com.aamo.cookbook.utility.toUUIDorNull
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import java.util.UUID

/**
 * Enum class for screen navigation
 */
enum class EditRecipeScreenPage(val route: String) {
  EditRecipeInfo("edit/info"),
  EditRecipeChapter("edit/chapter"),
  EditChapterStep("edit/step"),
  EditStepIngredient("edit/ingredient")
}

fun NavGraphBuilder.editRecipeGraph(
  viewModel: AppViewModel,
  navController: NavHostController,
  onSubmit: (Recipe) -> Unit = {},
  onDelete: (UUID) -> Unit = {}
) {
  composable(EditRecipeScreenPage.EditRecipeInfo.route) {
    val recipeId =
      navController.currentBackStackEntry?.arguments?.getString(Screen.EditRecipe.argumentName)
        ?.toUUIDorNull()
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navController)

    LaunchedEffect(recipeId) {
      if (recipeId != null && editRecipeViewModel.infoUiState.value.id != recipeId) {
        val recipe =
          viewModel.getRecipe(recipeId)
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
        navController.navigate(EditRecipeScreenPage.EditRecipeChapter.route)
      },
      onSubmitChanges = { onSubmit(editRecipeViewModel.infoUiState.value.toRecipe()) },
      onDelete = { onDelete(editRecipeViewModel.infoUiState.value.id) },
      onBack = { navController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditRecipeChapter.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navController)

    EditRecipeChapterScreen(
      viewModel = editRecipeViewModel,
      onEditStep = { stepIndex ->
        editRecipeViewModel.initStepUiState(
          step = editRecipeViewModel.chapterUiState.value.steps.elementAtOrNull(stepIndex),
          index = stepIndex
        )
        navController.navigate(route = EditRecipeScreenPage.EditChapterStep.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateChapter(
          chapter = editRecipeViewModel.chapterUiState.value.toChapter(),
          index = editRecipeViewModel.chapterUiState.value.index
        )
        navController.navigateUp()
      },
      onBack = { navController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditChapterStep.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navController)

    EditRecipeChapterStepScreen(
      viewModel = editRecipeViewModel,
      onEditIngredient = { ingredientIndex ->
        editRecipeViewModel.initIngredientUiState(
          ingredient = editRecipeViewModel.stepUiState.value.ingredients.elementAtOrNull(
            ingredientIndex
          ),
          index = ingredientIndex
        )
        navController.navigate(route = EditRecipeScreenPage.EditStepIngredient.route)
      },
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateStep(
          step = editRecipeViewModel.stepUiState.value.toStep(),
          index = editRecipeViewModel.stepUiState.value.index
        )
        navController.navigateUp()
      },
      onBack = { navController.navigateUp() }
    )
  }
  composable(EditRecipeScreenPage.EditStepIngredient.route) {
    val editRecipeViewModel =
      it.sharedViewModel<EditRecipeViewModel>(navController = navController)

    EditRecipeChapterStepIngredientScreen(
      viewModel = editRecipeViewModel,
      onSubmitChanges = {
        editRecipeViewModel.addOrUpdateIngredient(
          ingredient = editRecipeViewModel.ingredientUiState.value.toIngredient(),
          index = editRecipeViewModel.ingredientUiState.value.index
        )
        navController.navigateUp()
      },
      onBack = { navController.navigateUp() })
  }
}