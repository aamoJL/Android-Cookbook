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
import com.aamo.cookbook.features.recipe.list.RecipeSearchScreen
import com.aamo.cookbook.features.recipe.list.RecipesByCategoryScreen
import com.aamo.cookbook.features.recipe.list.recipeListPages

@Composable
fun HomePage() {
  val navController = rememberNavController()

  Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = navController,
      startDestination = HomeScreen,
      enterTransition = { fadeIn(animationSpec = tween(300, easing = LinearEasing)) },
      exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }) {
      homeScreen(onOpenSearch = {
        navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
      }, onOpenRecipeForm = { TODO() }, onOpenBookmarks = { TODO() }, onOpenRecipesByCategory = {
        navController.navigate(RecipesByCategoryScreen(category = it)) { launchSingleTop = true }
      })
      recipeListPages(onOpenRecipe = { TODO() }, onOpenSearch = {
        navController.navigate(RecipeSearchScreen) { launchSingleTop = true }
      }, onOpenRecipeForm = { TODO() }, onBack = { navController.navigateUp() })
    }
  }
}