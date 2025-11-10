package com.aamo.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.features.home.HomePage
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import kotlinx.coroutines.launch

/**
 * Enum class for screen navigation
 */
// TODO: remove
enum class Screen(private val route: String, val argumentName: String = "") {
  Categories("categories"),
  Recipes("recipes"),
  Recipe("recipe/", "recipeId"),
  Search("search"),
  Favorites("favorites");

  fun getRoute(): String = when (argumentName) {
    "" -> route
    else -> route.plus("{$argumentName}")
  }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CookbookTheme {
        val snackState = remember { SnackbarHostState() }
        val snackScope = rememberCoroutineScope()

        Box {
          HomePage(
            onShowSnackbar = { properties ->
              snackScope.launch {
                snackState.showSnackbar(
                  message = properties.message,
                  actionLabel = properties.actionLabel,
                  withDismissAction = properties.withDismissAction,
                  duration = properties.duration
                )
              }
            })
          SnackbarHost(hostState = snackState, Modifier.align(Alignment.BottomCenter))
        }
      }
    }
  }
}

// TODO: remove
@Composable
fun MainNavGraph(
  navController: NavHostController = rememberNavController(),
  onShowSnackbar: (SnackbarProperties) -> Unit = {}
) {
  val context = LocalContext.current

  NavHost(
    navController = navController,
    startDestination = Screen.Categories.getRoute(),
    enterTransition = { fadeIn(animationSpec = tween(300, easing = LinearEasing)) },
    exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }) {
    composable(route = Screen.Categories.getRoute()) {}
    composable(route = Screen.Recipes.getRoute()) {}
    composable(route = Screen.Favorites.getRoute()) {}
    composable(route = Screen.Search.getRoute()) {}
    composable(route = Screen.Recipe.getRoute()) {
//      RecipeScreen(
//        onBack = { navController.navigateUp() },
//        onEditRecipe = { id -> navController.navigate(Screen.EditRecipe.getRouteWithArgument(id.toString())) },
//        onCopyRecipe = { id ->
//          appViewModel.viewModelScope.launch {
//            appViewModel.getRecipeWithChaptersStepsAndIngredients(id)?.let { recipe ->
//              recipe.copyAsNew().let { recipeCopy ->
//                recipeCopy.copy(
//                  recipe = recipeCopy.recipe.copy(
//                    name = context.getString(
//                      R.string.recipe_name_copy, recipe.recipe.name
//                    )
//                  )
//                )
//              }
//            }?.also { copiedRecipe ->
//              appViewModel.upsertRecipe(copiedRecipe).also { newId ->
//                if (newId > 0) {
//                  navController.navigate(Screen.Recipe.getRouteWithArgument(newId.toString())) {
//                    popUpTo(Screen.Recipe.getRoute()) { inclusive = true }
//                  }
//                  onShowSnackbar(SnackbarProperties(context.getString(R.string.snackbar_recipe_copied_successfully)))
//                }
//              }
//            }
//          }
//        },
//        onShowSnackbar = onShowSnackbar
//      )
    }
  }
}