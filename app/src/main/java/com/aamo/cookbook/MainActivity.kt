package com.aamo.cookbook

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.aamo.cookbook.ui.screen.RecipeSearchScreen
import com.aamo.cookbook.ui.screen.RecipesScreen
import com.aamo.cookbook.ui.screen.editRecipe.editRecipeGraph
import com.aamo.cookbook.ui.screen.recipeScreen.RecipeScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import kotlinx.coroutines.launch

/**
 * Enum class for screen navigation
 */
enum class Screen(private val route: String, val argumentName: String = "") {
  Categories("categories"),
  Recipes("recipes"),
  Recipe("recipe/", "recipeId"),
  EditRecipe("edit/recipe/", "recipeId"),
  Search("search"),
  Favorites("favorites");

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

data class SnackbarProperties (
  val message: String,
  val actionLabel: String? = null,
  val withDismissAction: Boolean = false,
  val duration: SnackbarDuration = SnackbarDuration.Short
)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CookbookTheme {
        val snackState = remember { SnackbarHostState() }
        val snackScope = rememberCoroutineScope()

        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Box {
            MainNavGraph(
              onShowSnackbar = { properties ->
                snackScope.launch {
                  snackState.showSnackbar(
                    message = properties.message,
                    actionLabel = properties.actionLabel,
                    withDismissAction = properties.withDismissAction,
                    duration = properties.duration
                  )
                }
              }
            )
            SnackbarHost(hostState = snackState, Modifier.align(Alignment.BottomCenter))
          }
        }
      }
    }
  }
}

@Composable
fun MainNavGraph(
  appViewModel : AppViewModel = viewModel(factory = ViewModelProvider.Factory),
  navController : NavHostController = rememberNavController(),
  onShowSnackbar: (SnackbarProperties) -> Unit = {}
) {
  val context = LocalContext.current

  NavHost(
    navController = navController,
    startDestination = Screen.Categories.getRoute(),
    enterTransition = { fadeIn(animationSpec = tween(300, easing = LinearEasing)) },
    exitTransition = { fadeOut(animationSpec = tween(300, easing = LinearEasing)) }
  ) {
    composable(route = Screen.Categories.getRoute()) {
      val categories by appViewModel.getCategories().collectAsState(initial = emptyList())

      CategoriesScreen(
        categories = categories,
        onSelectCategory = {
          appViewModel.setSelectedCategory(it)
          navController.navigate(Screen.Recipes.getRoute())
        },
        onAddRecipe = { navController.navigate(Screen.EditRecipe.getRouteWithArgument("0")) },
        onSearch = { navController.navigate(Screen.Search.getRoute()) },
        onFavorites = { navController.navigate(Screen.Favorites.getRoute()) }
      )
    }
    composable(route = Screen.Recipes.getRoute()) {
      val category by appViewModel.selectedCategory.collectAsStateWithLifecycle()
      val recipes by appViewModel.getRecipesByCategory(category).collectAsStateWithLifecycle(
        initialValue = emptyList(),
      )
      val favorites by appViewModel.getFavoriteRecipes().collectAsStateWithLifecycle(
        initialValue = emptyList(),
      )

      RecipesScreen(
        title = category,
        recipes = recipes,
        onSelectRecipe = { recipe ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
        },
        onBack = { navController.navigateUp() },
        onSearch = { navController.navigate(Screen.Search.getRoute()) },
        favorites = favorites.map { it.id }
      )
    }
    composable(route = Screen.Favorites.getRoute()) {
      val favorites by appViewModel.getFavoriteRecipes().collectAsStateWithLifecycle(
        initialValue = emptyList(),
      )

      RecipesScreen(
        title = stringResource(R.string.button_text_favorites),
        recipes = favorites,
        onSelectRecipe = { recipe ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
        },
        onBack = { navController.navigateUp() },
        onSearch = { navController.navigate(Screen.Search.getRoute()) },
        favorites = favorites.map { it.id }
      )
    }
    composable(route = Screen.Search.getRoute()) {
      RecipeSearchScreen(
        onBack = { navController.navigateUp() },
        onSelect = { id ->
          navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString()))
        }
      )
    }
    composable(
      route = Screen.Recipe.getRoute(),
      arguments = listOf(navArgument(Screen.Recipe.argumentName) {
        type = NavType.IntType
      }),
      enterTransition = {
        fadeIn(
          animationSpec = tween(300, easing = LinearEasing)
        ) + slideIntoContainer(
          animationSpec = tween(300, easing = EaseIn),
          towards = AnimatedContentTransitionScope.SlideDirection.Start
        )
      },
      exitTransition = {
        fadeOut(
          animationSpec = tween(300, easing = LinearEasing)
        ) + slideOutOfContainer(
          animationSpec = tween(300, easing = EaseOut),
          towards = AnimatedContentTransitionScope.SlideDirection.End
        )
      }
    ) {
      RecipeScreen(
        onBack = { navController.navigateUp() },
        onEditRecipe = { id -> navController.navigate(Screen.EditRecipe.getRouteWithArgument(id.toString())) },
        onCopyRecipe = { id ->
          appViewModel.viewModelScope.launch {
            appViewModel.getRecipeWithChaptersStepsAndIngredients(id)?.let { recipe ->
              recipe.copy(
                value = Recipe(
                  name = context.getString(R.string.recipe_name_copy, recipe.value.name),
                  category = recipe.value.category,
                  subCategory = recipe.value.subCategory,
                  servings = recipe.value.servings
                ),
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
              appViewModel.upsertRecipe(recipe).also { newId ->
                if (newId > 0) {
                  navController.navigate(Screen.Recipe.getRouteWithArgument(newId.toString())) {
                    popUpTo(Screen.Recipe.getRoute()) { inclusive = true }
                  }
                  onShowSnackbar(SnackbarProperties(context.getString(R.string.snackbar_recipe_copied_successfully)))
                }
              }
            }
          }
        },
        onShowSnackbar = onShowSnackbar
      )
    }
    this.editRecipeGraph(
      screen = Screen.EditRecipe,
      navController = navController,
      onBack = { navController.navigateUp() },
      onSubmitChanges = {
        appViewModel.viewModelScope.launch {
          val id = appViewModel.upsertRecipe(it)
          appViewModel.setSelectedCategory(it.value.category)

          if (navController.previousBackStackEntry?.destination?.route == Screen.Recipe.getRoute()) {
            navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString())) {
              popUpTo(Screen.Recipe.getRoute()) { inclusive = true }
            }
          } else {
            navController.navigate(Screen.Recipe.getRouteWithArgument(id.toString())) {
              popUpTo(Screen.EditRecipe.getRoute()) { inclusive = true }
            }
          }
          onShowSnackbar(SnackbarProperties(context.getString(R.string.snackbar_recipe_saved_successfully)))
        }
      },
      onDeleteRecipe = {
        appViewModel.viewModelScope.launch {
          appViewModel.deleteRecipe(it)

          navController.navigate(Screen.Categories.getRoute()) {
            popUpTo(Screen.Categories.getRoute()) { inclusive = true }
          }
          onShowSnackbar(SnackbarProperties(context.getString(R.string.snackbar_recipe_deleted_successfully)))
        }
      },
    )
  }
}
