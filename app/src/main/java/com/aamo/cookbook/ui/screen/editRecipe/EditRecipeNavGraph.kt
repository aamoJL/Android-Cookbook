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
  EditRecipeInfo("edit/info"),
  EditRecipeChapter("edit/chapter"),
  EditChapterStep("edit/step"),
  EditStepIngredient("edit/ingredient")
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
    startDestination = EditRecipeScreenPage.EditRecipeInfo.route,
    arguments = listOf(navArgument(screen.argumentName) { type = NavType.IntType })
  ) {
      composable(EditRecipeScreenPage.EditRecipeInfo.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeScreen(
          viewModel = editRecipeViewModel,
          onEditChapter = { chapter ->
            editRecipeViewModel.initChapterUiState(chapter = chapter.value, steps = chapter.steps)
            navController.navigate(EditRecipeScreenPage.EditRecipeChapter.route) {
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
      composable(EditRecipeScreenPage.EditRecipeChapter.route) {
        val editRecipeViewModel =
          it.sharedViewModel<EditRecipeViewModel>(navController = navController)

        EditRecipeChapterScreen(
          viewModel = editRecipeViewModel,
          onEditStep = { step ->
            editRecipeViewModel.initStepUiState(step = step.value, ingredients = step.ingredients)
            navController.navigate(route = EditRecipeScreenPage.EditChapterStep.route) {
              launchSingleTop = true
            }
          },
          onSubmitChanges = {
            editRecipeViewModel.addOrUpdateChapter(
              chapter = editRecipeViewModel.chapterUiState.value.toChapterWithStepsAndIngredients()
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
            navController.navigate(route = EditRecipeScreenPage.EditStepIngredient.route) {
              launchSingleTop = true
            }
          },
          onSubmitChanges = {
            editRecipeViewModel.addOrUpdateStep(editRecipeViewModel.stepUiState.value.toStepWithIngredients())
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
}