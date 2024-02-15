package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry
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
  onBack: () -> Unit = {},
) {
  navigation(
    route = screen.getRoute(),
    startDestination = EditRecipeScreenPage.EditInfo.route,
    arguments = listOf(navArgument(screen.argumentName) { type = NavType.IntType }),
  ) {
    composable(
      route = EditRecipeScreenPage.EditInfo.route,
      enterTransition = {
        if (this.initialState.destination.route != EditRecipeScreenPage.EditChapter.route)
          this.primaryEnterTransition()
        else secondaryEnterTransition()
      },
      exitTransition = {
        if (this.targetState.destination.route == EditRecipeScreenPage.EditChapter.route)
          primaryExitTransition()
        else this.secondaryExitTransition()
      }
    ) {
      val editRecipeViewModel =
        it.sharedViewModel<EditRecipeViewModel>(navController = navController)

      EditRecipeScreen(
        viewModel = editRecipeViewModel,
        onEditChapter = { index ->
          editRecipeViewModel.initChapterUiState(index)
          navController.navigate(EditRecipeScreenPage.EditChapter.route) {
            launchSingleTop = true
          }
        },
        onSubmitChanges = {
          onSubmitChanges(editRecipeViewModel.toRecipeWithChaptersStepsAndIngredients())
        },
        onDelete = { onDeleteRecipe(editRecipeViewModel.toRecipeWithChaptersStepsAndIngredients().value) },
        onBack = onBack
      )
    }
    composable(
      route = EditRecipeScreenPage.EditChapter.route,
      enterTransition = {
        if (this.initialState.destination.route == EditRecipeScreenPage.EditInfo.route)
          this.primaryEnterTransition()
        else secondaryEnterTransition()
      },
      exitTransition = {
        if (this.targetState.destination.route == EditRecipeScreenPage.EditStep.route)
          primaryExitTransition()
        else this.secondaryExitTransition()
      }
    ) {
      val editRecipeViewModel =
        it.sharedViewModel<EditRecipeViewModel>(navController = navController)

      EditRecipeChapterScreen(
        viewModel = editRecipeViewModel,
        onEditStep = { index ->
          editRecipeViewModel.initStepUiState(index)
          navController.navigate(route = EditRecipeScreenPage.EditStep.route) {
            launchSingleTop = true
          }
        },
        onSubmitChanges = {
          editRecipeViewModel.applyChapterChanges()
          navController.navigate(route = EditRecipeScreenPage.EditInfo.route) {
            popUpTo(EditRecipeScreenPage.EditChapter.route) { inclusive = true }
            launchSingleTop = true
          }
        },
        onBack = { navController.navigateUp() }
      )
    }
    composable(
      route = EditRecipeScreenPage.EditStep.route,
      enterTransition = {
        if (this.initialState.destination.route == EditRecipeScreenPage.EditChapter.route)
          this.primaryEnterTransition()
        else secondaryEnterTransition()
      },
      exitTransition = {
        if (this.targetState.destination.route == EditRecipeScreenPage.EditIngredient.route)
          primaryExitTransition()
        else this.secondaryExitTransition()
      }
    ) {
      val editRecipeViewModel =
        it.sharedViewModel<EditRecipeViewModel>(navController = navController)

      EditRecipeChapterStepScreen(
        viewModel = editRecipeViewModel,
        onEditIngredient = { index ->
          editRecipeViewModel.initIngredientUiState(index)
          navController.navigate(route = EditRecipeScreenPage.EditIngredient.route) {
            launchSingleTop = true
          }
        },
        onSubmitChanges = {
          editRecipeViewModel.applyStepChanges()
          navController.navigate(route = EditRecipeScreenPage.EditChapter.route) {
            popUpTo(EditRecipeScreenPage.EditStep.route) { inclusive = true }
            launchSingleTop = true
          }
        },
        onBack = { navController.navigateUp() }
      )
    }
    composable(
      route = EditRecipeScreenPage.EditIngredient.route,
      enterTransition = { this.primaryEnterTransition() },
      exitTransition = { this.secondaryExitTransition() }
    ) {
      val editRecipeViewModel =
        it.sharedViewModel<EditRecipeViewModel>(navController = navController)

      EditRecipeChapterStepIngredientScreen(
        viewModel = editRecipeViewModel,
        onSubmitChanges = {
          editRecipeViewModel.applyIngredientChanges()
          navController.navigate(route = EditRecipeScreenPage.EditStep.route) {
            popUpTo(EditRecipeScreenPage.EditIngredient.route) { inclusive = true }
            launchSingleTop = true
          }
        },
        onBack = { navController.navigateUp() })
    }
  }
}

//region [Animation stuff]
private fun AnimatedContentTransitionScope<NavBackStackEntry>.primaryEnterTransition(): EnterTransition {
  return fadeIn(
    animationSpec = tween(300, easing = LinearEasing)
  ) + slideIntoContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.Start
  )
}

private fun secondaryEnterTransition(): EnterTransition {
  return fadeIn(
    animationSpec = tween(300, easing = LinearEasing)
  ) + scaleIn(
    animationSpec = tween(300, easing = EaseIn),
    initialScale = 0.9f
  )
}

private fun primaryExitTransition(): ExitTransition {
  return fadeOut(
    animationSpec = tween(300, easing = LinearEasing)
  ) + scaleOut(
    animationSpec = tween(300, easing = EaseOut),
    targetScale = 0.9f
  )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.secondaryExitTransition(): ExitTransition {
  return fadeOut(
    animationSpec = tween(300, easing = LinearEasing)
  ) + slideOutOfContainer(
    animationSpec = tween(300, easing = EaseOut),
    towards = AnimatedContentTransitionScope.SlideDirection.End
  )
}
//endregion