package com.aamo.cookbook.ui.screen.editRecipe

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.aamo.cookbook.Screen
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.utility.sharedViewModel
import com.aamo.cookbook.viewModel.EditRecipeViewModel

/**
 * Enum class for screen navigation
 */
enum class EditRecipeScreenPage(val route: String) {
  EditInfo("edit/info"),
  EditChapter("edit/chapter"),
  EditStep("edit/step"),
  EditIngredient("edit/ingredient")
}

fun NavGraphBuilder.editRecipeGraph(
  screen: Screen,
  navController: NavHostController,
  onSubmitChanges: (RecipeWithChaptersStepsAndIngredients) -> Unit = {},
  onDeleteRecipe: (Recipe) -> Unit = {},
  onBack: () -> Unit = {}
) {
  navigation(
    route = screen.getRoute(),
    startDestination = EditRecipeScreenPage.EditInfo.route,
    arguments = listOf(navArgument(screen.argumentName) { type = NavType.IntType })
  ) {
      composable(EditRecipeScreenPage.EditInfo.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeScreen(
          viewModel = editRecipeViewModel,
          onEditChapter = { chapter ->
            editRecipeViewModel.initChapterUiState(chapter = chapter)
            navController.navigate(EditRecipeScreenPage.EditChapter.route) {
              launchSingleTop = true
            }
          },
          onSubmitChanges = {
            onSubmitChanges(
              editRecipeViewModel.infoUiState.value
                .toRecipeWithChaptersStepsAndIngredients()
            )
          },
          onDelete = { onDeleteRecipe(editRecipeViewModel.infoUiState.value.toRecipe()) },
          onBack = onBack
        )
      }
      composable(EditRecipeScreenPage.EditChapter.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeChapterScreen(
          viewModel = editRecipeViewModel,
          onEditStep = { step ->
            editRecipeViewModel.initStepUiState(step = step)
            navController.navigate(route = EditRecipeScreenPage.EditStep.route) {
              launchSingleTop = true
            }
          },
          onSubmitChanges = {
            editRecipeViewModel.addOrUpdateChapter(
              chapter = editRecipeViewModel.chapterUiState.value.toChapterWithStepsAndIngredients()
            )
            navController.navigate(route = EditRecipeScreenPage.EditInfo.route) {
              popUpTo(EditRecipeScreenPage.EditChapter.route) { inclusive = true }
              launchSingleTop = true
            }
          },
          onBack = { navController.navigateUp() }
        )
      }
      composable(EditRecipeScreenPage.EditStep.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeChapterStepScreen(
          viewModel = editRecipeViewModel,
          onEditIngredient = { ingredient ->
            editRecipeViewModel.initIngredientUiState(
              ingredient = ingredient,
              index = editRecipeViewModel.stepUiState.value.ingredients.indexOf(ingredient)
                .let { index ->
                  if (index == -1) editRecipeViewModel.stepUiState.value.ingredients.size
                  else index
                }
            )
            navController.navigate(route = EditRecipeScreenPage.EditIngredient.route) {
              launchSingleTop = true
            }
          },
          onSubmitChanges = {
            editRecipeViewModel.addOrUpdateStep(editRecipeViewModel.stepUiState.value.toStepWithIngredients())
            navController.navigate(route = EditRecipeScreenPage.EditChapter.route) {
              popUpTo(EditRecipeScreenPage.EditStep.route) { inclusive = true }
              launchSingleTop = true
            }
          },
          onBack = { navController.navigateUp() }
        )
      }
      composable(EditRecipeScreenPage.EditIngredient.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeChapterStepIngredientScreen(
          viewModel = editRecipeViewModel,
          onSubmitChanges = {
            editRecipeViewModel.addOrUpdateIngredient(
              ingredient = editRecipeViewModel.ingredientUiState.value.toIngredient(),
              index = editRecipeViewModel.ingredientUiState.value.index
            )
            navController.navigate(route = EditRecipeScreenPage.EditStep.route) {
              popUpTo(EditRecipeScreenPage.EditIngredient.route) { inclusive = true }
              launchSingleTop = true
            }
          },
          onBack = { navController.navigateUp() })
      }
    }
}