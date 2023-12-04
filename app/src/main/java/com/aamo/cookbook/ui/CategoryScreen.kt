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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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

enum class Screens() {
  Categories(),
  Recipes(),
  Recipe(),
  NewRecipe(),
}

class AppViewModel : ViewModel(){
  class AppUiState(){
    data class UiState(
      val selectedCategory: String? = null,
      val selectedRecipe: Recipe? = null,
      val nextRecipeStepButtonEnabled: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setCategory(category: String?){
      _uiState.update { currentState -> currentState.copy(selectedCategory = category) }
    }

    fun setRecipe(recipe: Recipe?){
      _uiState.update { currentState -> currentState.copy(selectedRecipe = recipe) }
    }
  }

  val appUiState = AppUiState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CookBookApp(
  viewModel: AppViewModel = viewModel(),
  navController: NavHostController = rememberNavController())
{
  val uiState by viewModel.appUiState.uiState.collectAsState()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route
  val chapterIndex = backStackEntry?.arguments?.getInt("chapterIndex")

  LaunchedEffect(uiState.selectedCategory != null
          && (currentRoute == null || currentRoute == Screens.Categories.name)){
    viewModel.appUiState.setCategory(null)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(getScreenTitle(currentRoute ?: Screens.Categories.name, uiState))
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
          actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
          navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
          if (navController.previousBackStackEntry != null){
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
      if(currentRoute == Screens.Categories.name || currentRoute == Screens.Recipes.name){
        FloatingActionButton(
          onClick = { navController.navigate(Screens.NewRecipe.name) },
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
        startDestination = Screens.Categories.name
      ) {
        composable(Screens.Categories.name) {
          CategoryList(categories = RecipeCategoryRepository().loadCategories(),
            onSelect = {
              viewModel.appUiState.setCategory(it.name)
              navController.navigate(Screens.Recipes.name)
            })
        }
        composable(Screens.Recipes.name) {
          if(uiState.selectedCategory != null){
            RecipesScreen(
              recipes = RecipeRepository().loadRecipes(uiState.selectedCategory!!),
              onSelect = { recipe ->
                viewModel.appUiState.setRecipe(recipe)
                navController.navigate("${Screens.Recipe.name}/0")
              }
            )
          }
        }
        composable("${Screens.Recipe.name}/{chapterIndex}",
          arguments = listOf(navArgument("chapterIndex") { type = NavType.IntType })
        ){_ ->
          if(uiState.selectedRecipe != null && chapterIndex != null){
            RecipeScreen().Screen(uiState.selectedRecipe!!)
          }
        }
        composable(Screens.NewRecipe.name){
          NewRecipeScreen(uiState.selectedCategory ?: "")
        }
      }
    },
    bottomBar = {

    }
  )
}

@Composable fun CategoryList(categories: List<RecipeCategory>, onSelect: (RecipeCategory) -> Unit) {
  Column {
    Divider(color = MaterialTheme.colorScheme.secondary)
    LazyColumn() {
      items(categories) { category ->
        CategoryItem(
          category = category,
          onClick = { onSelect(category) },
          modifier = Modifier.fillMaxWidth())
        Divider(color = MaterialTheme.colorScheme.secondary)
      }
    }
  }
}

@Composable fun CategoryItem(
  category: RecipeCategory,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.clickable(onClick = onClick)) {
    Text(text = (if (category.name != "") category.name else "Muut"),
      style = MaterialTheme.typography.titleLarge,
      modifier = modifier.padding(20.dp, 40.dp, 20.dp, 40.dp))
  }
}

fun getScreenTitle(route: String, appUiState: AppViewModel.AppUiState.UiState) : String{
  return when(route){
    Screens.Categories.name -> "Valitse kategoria"
    Screens.Recipes.name -> appUiState.selectedCategory ?: ""
    "${Screens.Recipe.name}/{chapterIndex}" -> appUiState.selectedRecipe?.name ?: ""
    Screens.NewRecipe.name -> "Uusi resepti"
    else -> ""
  }
}