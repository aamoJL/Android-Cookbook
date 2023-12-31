package com.aamo.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aamo.cookbook.ui.screen.CategoriesScreen
import com.aamo.cookbook.ui.screen.RecipeScreen
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPage
import com.aamo.cookbook.ui.screen.editRecipe.editRecipeGraph
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.toUUIDorNull
import com.aamo.cookbook.viewModel.AppViewModel
import java.util.UUID

/**
 * Enum class for screen navigation
 */
enum class Screen(private val route: String, val argumentName: String = "") {
  Categories("categories"),
  Recipes("recipes/", "categoryName"),
  Recipe("recipe/", "recipeId"),
  EditRecipe("edit/recipe/", "recipeId");

  fun getRoute(): String = when (argumentName) {
    "" -> route
    else -> route.plus("{$argumentName}")
  }

  fun getRouteWithArgument(argument: String): String = route.plus(argument)
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val viewModel = AppViewModel()
      val navController = rememberNavController()

      CookbookTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(
            navController = navController,
            startDestination = Screen.Categories.getRoute()
          ) {
            composable(Screen.Categories.getRoute()) {
              val categories = viewModel.getCategories()

              CategoriesScreen(
                categories = categories,
                onSelect = {
                  navController.navigate(
                    Screen.Recipes.getRouteWithArgument(it)
                  )
                },
                onAddClick = {
                  navController.navigate(
                    Screen.EditRecipe.getRoute()
                  )
                })
            }
            composable(
              Screen.Recipes.getRoute(),
              arguments = listOf(navArgument(Screen.Recipes.argumentName) {
                type = NavType.StringType
                defaultValue = ""
              })
            ) {
              val category = it.arguments!!.getString(Screen.Recipes.argumentName)!!
              val recipes = viewModel.getRecipes(category)

              RecipesScreen(
                recipes = recipes,
                onSelect = { recipe ->
                  navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
                },
                onBack = { navController.navigateUp() }
              )
            }
            composable(
              Screen.Recipe.getRoute(),
              arguments = listOf(navArgument(Screen.Recipe.argumentName) {
                type = NavType.StringType
                defaultValue = UUID(0, 0).toString()
              })
            ) {
              val recipeId = it.arguments!!.getString(Screen.Recipe.argumentName)!!.toUUIDorNull()
                ?: UUID(0, 0)

              RecipeScreen(recipeId, onBack = { navController.navigateUp() })
            }
            navigation(
              startDestination = EditRecipeScreenPage.EditRecipeInfo.route,
              route = Screen.EditRecipe.getRoute(),
              arguments = listOf(navArgument(Screen.EditRecipe.argumentName) {
                type = NavType.StringType
              })
            ) {
              this.editRecipeGraph(navController)
            }
          }
        }
      }
    }
  }
}