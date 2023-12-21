package com.aamo.cookbook.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategory
import com.aamo.cookbook.repository.RecipeCategoryRepository
import com.aamo.cookbook.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Enum class for screen navigation
 */
enum class Screen(val route: String, val argumentName: String = "") {
  Categories("categories"),
  Recipes("recipes/{categoryName}", "categoryName"),
  Recipe("recipe/{recipeId}", "recipeId"),
  EditRecipe("edit/recipe/{recipeId}", "recipeId"),
  EditRecipeInfo("edit/recipe/info"),
  EditRecipeChapter("edit/chapter/{chapterIndex}", "chapterIndex"),
  EditChapterStep("edit/step/{stepIndex}", "stepIndex"),
  ;

  fun getRouteWithArgument(argument: String): String =
    route.replace("{${argumentName}}", argument)
}

class AppViewModel : ViewModel(){
  class AppUiState {
    data class UiState(
      val nextRecipeStepButtonEnabled: Boolean = false,
      val title: String = "",
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val recipeRepository = RecipeRepository()

    fun setTitle(title: String){
      _uiState.update { currentState -> currentState.copy(title = title)}
    }
  }

  val appUiState = AppUiState()
}

class CategoryScreen {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun Screen(
    viewModel: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
  ) {
    val uiState by viewModel.appUiState.uiState.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(text = uiState.title)
          },
          colors = TopAppBarDefaults.smallTopAppBarColors(
            actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.primaryContainer,
          ),
          navigationIcon = {
            if (navController.previousBackStackEntry != null) {
              IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                  imageVector = Icons.Filled.ArrowBack,
                  contentDescription = "Back button"
                )
              }
            }
          },
        )
      },
      floatingActionButton = {
        if (currentRoute == Screen.Categories.route || currentRoute == Screen.Recipes.route) {
          FloatingActionButton(
            onClick = { navController.navigate(Screen.EditRecipe.route) },
            content = {
              Icon(Icons.Filled.Add, "Floating action button.")
            }
          )
        }
      },
      floatingActionButtonPosition = FabPosition.End,
      content = { innerPadding ->
        NavHost(
          modifier = Modifier.padding(innerPadding),
          navController = navController,
          startDestination = Screen.Categories.route
        ) {
          composable(Screen.Categories.route) {
            TitleEffect(title = "Valitse kategoria", uiState = viewModel.appUiState)
//            LaunchedEffect(true) {
//              viewModel.appUiState.setCategory(null)
//            }

            CategoryList(categories = RecipeCategoryRepository().loadCategories(),
              onSelect = {
                navController.navigate(
                  Screen.Recipes.getRouteWithArgument(it.name)
                )
              })
          }
          composable(
            Screen.Recipes.route,
            arguments = listOf(navArgument(Screen.Recipes.argumentName) {
              type = NavType.StringType
            })
          ) {
            val category =
              remember(it) { backStackEntry?.arguments?.getString(Screen.Recipes.argumentName) }

            TitleEffect(
              title = category ?: "Kategoriaa ei löytynyt",
              uiState = viewModel.appUiState
            )

            if (category != null) {
              RecipesScreen(
                recipes = viewModel.appUiState.recipeRepository.getRecipes(category),
                onSelect = { recipe ->
                  navController.navigate(Screen.Recipe.getRouteWithArgument(recipe.id.toString()))
                }
              )
            }
          }
          composable(
            Screen.Recipe.route,
            arguments = listOf(navArgument(Screen.Recipe.argumentName) {
              type = NavType.StringType
            })
          ) {
            val recipeId = remember(it) {
              backStackEntry?.arguments?.getString(Screen.Recipe.argumentName)?.toUUIDorNull()
            }
            val recipe = remember(it) { viewModel.appUiState.recipeRepository.getRecipe(recipeId) }

            TitleEffect(
              title = recipe?.name ?: "Valittua reseptiä ei löytynyt",
              uiState = viewModel.appUiState
            )

            if (recipe != null) {
              RecipeScreen().Screen(recipe)
            }
          }
          navigation(
            startDestination = Screen.EditRecipeInfo.route,
            route = Screen.EditRecipe.route,
            arguments = listOf(navArgument(Screen.EditRecipe.argumentName) {
              type = NavType.StringType
            })
          ) {
            composable(Screen.EditRecipeInfo.route) {
              val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.EditRecipe.route)
              }
              val recipeIdArgument =
                parentEntry.arguments?.getString(Screen.EditRecipe.argumentName)
              val editRecipeViewModel =
                it.sharedViewModel<EditRecipeViewModel>(navController = navController)

              TitleEffect(title = "Muokkaa reseptiä", uiState = viewModel.appUiState)
              LaunchedEffect(true) {
                val recipe =
                  viewModel.appUiState.recipeRepository.getRecipe(recipeIdArgument?.toUUIDorNull())
                if (recipe != null)
                  editRecipeViewModel.initInfoUiState(recipe)
              }

              EditRecipeScreen().Screen(
                editRecipeViewModel,
                onEditChapter = { index ->
                  navController.navigate(Screen.EditRecipeChapter.getRouteWithArgument(index.toString()))
                })
            }
            composable(
              route = Screen.EditRecipeChapter.route,
              arguments = listOf(navArgument(Screen.EditRecipeChapter.argumentName) {
                type = NavType.IntType
              })
            ) {
              val editRecipeViewModel =
                it.sharedViewModel<EditRecipeViewModel>(navController = navController)
              val chapterIndex =
                navController.currentBackStackEntry?.arguments?.getInt(Screen.EditRecipeChapter.argumentName)

              if (chapterIndex != null) {
                TitleEffect(title = "Muokkaa kappaletta", uiState = viewModel.appUiState)

                EditRecipeChapterScreen().Screen(
                  viewModel = editRecipeViewModel,
                  chapterIndex = chapterIndex,
                  onEditStep = { stepIndex ->
                    navController.navigate(
                      route = Screen.EditChapterStep.getRouteWithArgument(
                        stepIndex.toString()
                      )
                    )
                  }
                )
              }
            }
            composable(
              route = Screen.EditChapterStep.route,
              arguments = listOf(navArgument(Screen.EditChapterStep.argumentName) {
                type = NavType.IntType
              })
            ) {
              val editRecipeViewModel =
                it.sharedViewModel<EditRecipeViewModel>(navController = navController)
              val chapterIndex =
                navController.previousBackStackEntry?.arguments?.getInt(Screen.EditRecipeChapter.argumentName)
              val stepIndex =
                navController.currentBackStackEntry?.arguments?.getInt(Screen.EditRecipeChapter.argumentName)

              if(chapterIndex != null && stepIndex != null){
                TitleEffect(title = "Muokkaa vaihetta", uiState = viewModel.appUiState)
                EditRecipeChapterStepScreen().Screen(
                  viewModel = editRecipeViewModel,
                  chapterIndex = chapterIndex,
                  stepIndex = stepIndex,
                )
              }
            }
          }
        }
      },
      bottomBar = {

      }
    )
  }

  @Composable
  fun CategoryList(categories: List<RecipeCategory>, onSelect: (RecipeCategory) -> Unit) {
    Column {
      Divider(color = MaterialTheme.colorScheme.secondary)
      LazyColumn() {
        items(categories) { category ->
          CategoryItem(
            category = category,
            onClick = { onSelect(category) },
            modifier = Modifier.fillMaxWidth()
          )
          Divider(color = MaterialTheme.colorScheme.secondary)
        }
      }
    }
  }

  @Composable
  fun CategoryItem(
    category: RecipeCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    Box(modifier = modifier.clickable(onClick = onClick)) {
      Text(
        text = (if (category.name != "") category.name else "Muut"),
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(20.dp, 40.dp, 20.dp, 40.dp)
      )
    }
  }
}




// ------
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
  val navGraphRoute = destination.parent?.route ?: return viewModel()
  val parentEntry = remember(this){
    navController.getBackStackEntry(navGraphRoute)
  }
  return viewModel(parentEntry)
}

/**
 * LaunchedEffect that changes the app's title ui state
 */
@Composable
fun TitleEffect(title: String, uiState: AppViewModel.AppUiState, key: Any? = true) {
  LaunchedEffect(key){
    uiState.setTitle(title)
  }
}

/**
 * Returns the string as a [UUID] if possible, otherwise returns null
 */
fun String.toUUIDorNull() : UUID? {
  return try {
    UUID.fromString(this)
  }
  catch (e: IllegalArgumentException){
    null
  }
}

class EditRecipeViewModel : ViewModel() {
  data class InfoScreenUiState(
    val name: String = "",
    val category: String = "",
    val subCategory: String = "",
    val servings: Int = 1,
  )

  data class ChapterScreenUiState(
    val name: String = "",
    val steps: List<String> = listOf()
  )

  private val _infoUiState = MutableStateFlow(InfoScreenUiState())
  val infoUiState: StateFlow<InfoScreenUiState> = _infoUiState.asStateFlow()

  private val _chapterUiState = MutableStateFlow(ChapterScreenUiState())
  val chapterUiState: StateFlow<ChapterScreenUiState> = _chapterUiState.asStateFlow()

  private var recipeId: UUID = UUID(0,0)

  fun initInfoUiState(recipe: Recipe) {
    setId(recipe.id)
    setRecipeName(recipe.name)
    setCategory(recipe.category)
    setSubCategory(recipe.subCategory)
    setServings(recipe.servings)
  }

  fun initChapterUiState(chapter: Recipe.Chapter){
    setChapterName(chapter.name)
  }

  private fun setId(value: UUID) { recipeId = value }
  fun setRecipeName(value: String) = _infoUiState.update { s -> s.copy(name = value) }
  fun setCategory(value: String) = _infoUiState.update { s -> s.copy(category = value) }
  fun setSubCategory(value: String) = _infoUiState.update { s -> s.copy(subCategory = value) }
  fun setServings(value: Int) = _infoUiState.update { s -> s.copy(servings = value) }
  fun setChapterName(value: String) = _chapterUiState.update { s -> s.copy(name = value) }

  //fun setStep(value: String, index: Int) = _infoUiState.update { s -> s.copy(servings = value) }
}