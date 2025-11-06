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
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.RecipeFormPage
import com.aamo.cookbook.features.recipe.form.recipeFormPage
import com.aamo.cookbook.features.recipe.list.recipeListPages
import com.aamo.cookbook.features.recipe.list.screens.RecipeSearchScreen
import com.aamo.cookbook.features.recipe.list.screens.RecipesByBookmarkScreen
import com.aamo.cookbook.features.recipe.list.screens.RecipesByCategoryScreen
import com.aamo.cookbook.utility.SnackbarProperties

@Composable
fun HomePage(onShowSnackbar: (SnackbarProperties) -> Unit) {
  val navController = rememberNavController()
  val recipeDeletedSnackbarMessage = stringResource(R.string.snackbar_recipe_deleted_successfully)

  Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = navController,
      startDestination = HomeScreen,
      enterTransition = { fadeIn(animationSpec = tween(300, easing = LinearEasing)) },
      exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }) {
      homeScreen(onOpenSearch = {
        navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
      }, onOpenRecipeForm = {
        navController.navigate(RecipeFormPage(id = 0)) { launchSingleTop = true }
      }, onOpenBookmarks = {
        navController.navigate(RecipesByBookmarkScreen) { launchSingleTop = true }
      }, onOpenRecipesByCategory = {
        navController.navigate(RecipesByCategoryScreen(category = it)) { launchSingleTop = true }
      })
      recipeListPages(onOpenRecipe = { TODO("onOpenRecipe") }, onOpenSearch = {
        navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
      }, onOpenRecipeForm = {
        navController.navigate(RecipeFormPage(id = 0)) { launchSingleTop = true }
      }, onBack = { navController.navigateUp() })
      recipeFormPage(onBack = { navController.navigateUp() }, onOpenCategories = {
        navController.navigate(HomeScreen) { popUpTo(HomeScreen) { inclusive = true } }.also {
          onShowSnackbar(SnackbarProperties(recipeDeletedSnackbarMessage))
        }
      }, onOpenRecipe = {
        TODO("onOpenRecipe")
      })
    }
  }
}