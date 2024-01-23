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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import com.aamo.cookbook.ui.screen.CategoriesScreen
import com.aamo.cookbook.ui.screen.RecipeScreen
import com.aamo.cookbook.ui.screen.RecipeSearchScreen
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.screen.SubCategoriesScreen
import com.aamo.cookbook.ui.screen.editRecipe.editRecipeGraph
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Enum class for screen navigation
 */
enum class Screen(private val route: String, val argumentName: String = "") {
  Categories("categories"),
  SubCategories("subCategories"),
  Recipes("recipes"),
  Recipe("recipe/", "recipeId"),
  EditRecipe("edit/recipe/", "recipeId"),
  Search("search");

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
      val categories by viewModel.getCategories().collectAsState(initial = emptyList())

      CategoriesScreen(
        categories = categories,
        onSelectCategory = {
          viewModel.setSelectedCategory(it)
          navController.navigate(Screen.SubCategories.getRoute())
        },
        onAddRecipe = { navController.navigate(Screen.EditRecipe.getRouteWithArgument("0")) },
        onSearch = {navController.navigate(Screen.Search.getRoute())})
    }
    composable(Screen.SubCategories.getRoute()) {
      val subCategories by viewModel.getSubCategories(viewModel.selectedCategory
        .collectAsState().value)
        .collectAsState(initial = emptyList())

      SubCategoriesScreen(
        subCategories = subCategories,
        onSelectSubCategory = {
          viewModel.setSelectedSubCategory(it)
          navController.navigate(Screen.Recipes.getRoute())
        },
        onBack = { navController.navigateUp() },
        onSearch = {navController.navigate(Screen.Search.getRoute())}
      )
    }
    composable(Screen.Recipes.getRoute()) {
      val recipes by combine(
        viewModel.selectedCategory,
        viewModel.selectedSubCategory
      ) { category, subCategory ->
        viewModel.getRecipesBySubCategory(category, subCategory).first()
      }.collectAsStateWithLifecycle(initialValue = emptyList())

      RecipesScreen(
        recipes = recipes,
        onSelectRecipe = { recipe ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
        },
        onBack = { navController.navigateUp() },
        onSearch = {navController.navigate(Screen.Search.getRoute())}
      )
    }
    composable(Screen.Search.getRoute()) {
      RecipeSearchScreen(
        onBack = { navController.navigateUp() },
        onSelect = { id ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString()))
        }
      )
    }
    composable(
      Screen.Recipe.getRoute(),
      arguments = listOf(navArgument(Screen.Recipe.argumentName) {
        type = NavType.IntType
      })
    ) {
      val context = LocalContext.current

      RecipeScreen(
        onBack = { navController.navigateUp() },
        onEditRecipe = { id -> navController.navigate(Screen.EditRecipe.getRouteWithArgument(id.toString())) },
        onCopyRecipe = { id ->
          viewModel.viewModelScope.launch {
            viewModel.getRecipeWithChaptersStepsAndIngredients(id)?.let { recipe ->
              recipe.copy(
                value = Recipe(
                  name = context.getString(R.string.recipe_name_copy, recipe.value.name),
                  category = recipe.value.category,
                  subCategory = recipe.value.subCategory,
                  servings = recipe.value.servings),
                chapters = recipe.chapters.map { chapter ->
                  ChapterWithStepsAndIngredients(
                    value = Chapter(name = chapter.value.name),
                    steps = chapter.steps.map { step ->
                      StepWithIngredients(
                        value = Step(description = step.value.description),
                        ingredients = step.ingredients.map {
                          Ingredient(name = it.name, amount = it.amount, unit = it.unit)
                        }
                      )
                    }
                  )
                }
              )
            }?.also { recipe ->
              viewModel.upsertRecipe(recipe).also { newId ->
                if (newId > 0)
                  navController.navigate(Screen.Recipe.getRouteWithArgument(newId.toString())) {
                    popUpTo(Screen.Recipe.getRoute()) { inclusive = true }
                  }
              }
            }
          }
        }
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
