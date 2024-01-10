package com.aamo.cookbook

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aamo.cookbook.ui.screen.CategoriesScreen
import com.aamo.cookbook.ui.screen.RecipeScreen
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.screen.editRecipe.editRecipeGraph
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.launch

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

class CookbookApplication : Application() {
  lateinit var container: AppContainer

  override fun onCreate() {
    super.onCreate()
    container = AppDataContainer(this)
  }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CookbookTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavGraph()
        }
      }
    }
  }
}

@Composable
fun MainNavGraph(
  viewModel : AppViewModel = viewModel(factory = ViewModelProvider.Factory),
  navController : NavHostController = rememberNavController()
) {
  NavHost(
    navController = navController,
    startDestination = Screen.Categories.getRoute()
  ) {
    composable(Screen.Categories.getRoute()) {
      CategoriesScreen(
        categories = viewModel.getCategories().collectAsState(initial = emptyList()).value,
        onSelectCategory = {
          viewModel.setSelectedCategory(it)
          navController.navigate(Screen.Recipes.getRoute())
        },
        onAddRecipeClick = { navController.navigate(Screen.EditRecipe.getRouteWithArgument("0")) })
    }
    composable(Screen.Recipes.getRoute()) {
      val category by viewModel.selectedCategory.collectAsState()
      val recipes by viewModel.getRecipesByCategory(category).collectAsState(initial = emptyList())

      RecipesScreen(
        recipes = recipes,
        onSelectRecipe = { recipe ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
        },
        onBack = { navController.navigateUp() }
      )
    }
    composable(
      Screen.Recipe.getRoute(),
      arguments = listOf(navArgument(Screen.Recipe.argumentName) {
        type = NavType.IntType
      })
    ) {
      RecipeScreen(
        onBack = { navController.navigateUp() },
        onEditRecipe = { id -> navController.navigate(Screen.EditRecipe.getRouteWithArgument(id.toString())) }
      )
    }
    this.editRecipeGraph(
      screen = Screen.EditRecipe,
      navController = navController,
      onBack = { navController.navigateUp() },
      onSubmitChanges = {
        viewModel.viewModelScope.launch {
          val id = viewModel.upsertRecipe(it)
          viewModel.setSelectedCategory(it.value.category)

          if(navController.previousBackStackEntry?.destination?.route == Screen.Recipe.getRoute()){
            navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString())) {
              popUpTo(Screen.Recipe.getRoute()) { inclusive = true }
            }
          }
          else{
            navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString())) {
              popUpTo(Screen.EditRecipe.getRoute()) { inclusive = true }
            }
          }
        }
      },
      onDeleteRecipe = {
        viewModel.viewModelScope.launch {
          viewModel.deleteRecipe(it)
          navController.navigate(Screen.Categories.getRoute()) {
            popUpTo(Screen.Categories.getRoute()) { inclusive = true }
          }
        }
      }
    )
  }
}
