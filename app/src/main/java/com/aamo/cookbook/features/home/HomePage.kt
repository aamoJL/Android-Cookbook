package com.aamo.cookbook.features.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.features.recipe.form.RecipeFormPage
import com.aamo.cookbook.features.recipe.form.recipeFormPage
import com.aamo.cookbook.features.recipe.list.recipeListPages
import com.aamo.cookbook.features.recipe.list.screens.RecipeSearchScreen
import com.aamo.cookbook.features.recipe.list.screens.RecipesByBookmarkScreen
import com.aamo.cookbook.features.recipe.list.screens.RecipesByCategoryScreen
import com.aamo.cookbook.features.recipe.view.RecipeViewPage
import com.aamo.cookbook.features.recipe.view.recipeViewPage
import com.aamo.cookbook.utility.SnackbarProperties

@Composable
fun HomePage(onShowSnackbar: (SnackbarProperties) -> Unit) {
  val navController = rememberNavController()

  Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = navController,
      startDestination = HomeScreen,
      enterTransition = { fadeIn(animationSpec = tween(300, easing = LinearEasing)) },
      exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }) {
      homeScreen(
        onOpenSearch = {
          navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
        },
        onOpenRecipeForm = {
          navController.navigate(RecipeFormPage(id = 0)) { launchSingleTop = true }
        },
        onOpenBookmarks = {
          navController.navigate(RecipesByBookmarkScreen) { launchSingleTop = true }
        },
        onOpenRecipesByCategory = {
          navController.navigate(RecipesByCategoryScreen(category = it)) { launchSingleTop = true }
        },
      )
      recipeListPages(
        onOpenRecipe = {
          navController.navigate(RecipeViewPage(id = it)) { launchSingleTop = true }
        },
        onOpenSearch = {
          navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
        },
        onOpenRecipeForm = {
          navController.navigate(RecipeFormPage(id = 0)) { launchSingleTop = true }
        },
        onBack = { navController.navigateUp() },
      )
      recipeFormPage(
        onOpenCategories = {
          navController.navigate(HomeScreen) {
            popUpTo<HomeScreen> { inclusive = true }
            launchSingleTop = true
          }
        },
        onOpenRecipe = { id ->
          navController.popBackStack(route = RecipeFormPage::class, inclusive = true)
          navController.popBackStack(route = RecipeViewPage::class, inclusive = true)

          navController.navigate(RecipeViewPage(id = id)) {
            launchSingleTop = true
          }
        },
        onSnackbar = onShowSnackbar,
        onBack = { navController.navigateUp() },
      )
      recipeViewPage(
        onOpenRecipeForm = {
          navController.navigate(RecipeFormPage(id = it)) { launchSingleTop = true }
        },
        onSnackbar = onShowSnackbar,
        onBack = { navController.navigateUp() },
      )
    }
  }
}