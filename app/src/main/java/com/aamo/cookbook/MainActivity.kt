package com.aamo.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.repository.RecipeRepository
import com.aamo.cookbook.ui.screen.CategoriesScreen
import com.aamo.cookbook.ui.screen.RecipeScreen
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPage
import com.aamo.cookbook.ui.screen.editRecipe.editRecipeGraph
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.toUUIDorNull
import java.util.UUID

/**
 * Enum class for screen navigation
 */
enum class Screen(val route: String, val argumentName: String = "") {
  Categories("categories"),
  Recipes("recipes/{categoryName}", "categoryName"),
  Recipe("recipe/{recipeId}", "recipeId"),
  EditRecipe("edit/recipe/{recipeId}", "recipeId");

  fun getRouteWithArgument(argument: String): String =
    route.replace("{${argumentName}}", argument)
}

class AppViewModel : ViewModel() {
  fun getCategories(): List<String> = Repositories.recipeRepository.getRecipes().distinctBy {
    it.category
  }.map { recipe -> recipe.category }

  fun getRecipes(category: String): List<Recipe> =
    Repositories.recipeRepository.getRecipes(category)

  fun getRecipe(recipeId: UUID): Recipe? = Repositories.recipeRepository.getRecipe(recipeId)

  object Repositories {
    val recipeRepository = RecipeRepository()
  }
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
            startDestination = Screen.Categories.route
          ) {
            composable(Screen.Categories.route) {
              val categories = viewModel.getCategories()

              CategoriesScreen().Screen(
                categories = categories,
                onSelect = {
                  navController.navigate(
                    Screen.Recipes.getRouteWithArgument(it)
                  )
                },
                onAddClick = {
                  navController.navigate(
                    Screen.EditRecipe.route
                  )
                })
            }
            composable(
              Screen.Recipes.route,
              arguments = listOf(navArgument(Screen.Recipes.argumentName) {
                type = NavType.StringType
                defaultValue = ""
              })
            ) {
              val category = it.arguments!!.getString(Screen.Recipes.argumentName)!!
              val recipes = viewModel.getRecipes(category)

              RecipesScreen().Screen(
                recipes = recipes,
                onSelect = { recipe ->
                  navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
                },
                onBack = { navController.navigateUp() }
              )
            }
            composable(
              Screen.Recipe.route,
              arguments = listOf(navArgument(Screen.Recipe.argumentName) {
                type = NavType.StringType
                defaultValue = UUID(0, 0).toString()
              })
            ) {
              val recipeId = it.arguments!!.getString(Screen.Recipe.argumentName)!!.toUUIDorNull()
                ?: UUID(0, 0)

              RecipeScreen().Screen(recipeId, onBack = { navController.navigateUp() })
            }
            navigation(
              startDestination = EditRecipeScreenPage.EditRecipeInfo.route,
              route = Screen.EditRecipe.route,
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