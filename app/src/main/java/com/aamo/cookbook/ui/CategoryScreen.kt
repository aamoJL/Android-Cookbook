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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategory
import com.aamo.cookbook.repository.RecipeCategoryRepository
import com.aamo.cookbook.repository.RecipeRepository
import com.aamo.cookbook.ui.theme.RecipeScreen

enum class Screens(val title: String) {
  Categories("Valitse kategoria"),
  Recipes("Reseptit"),
  Recipe("Resepti"),
  NewRecipe("Uusi resepti"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CookBookApp(modifier: Modifier = Modifier) {
  var selectedCategory by remember { mutableStateOf("") }
  var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
  val categories = RecipeCategoryRepository().loadCategories()
  val navController = rememberNavController()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = backStackEntry?.destination?.route
  val canNavigateBack = navController.previousBackStackEntry != null
  val currentScreen = Screens.valueOf(
    backStackEntry?.destination?.route ?: Screens.Categories.name
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(currentScreen.title)
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
          actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
          navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer,
          containerColor = MaterialTheme.colorScheme.primary,
          titleContentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
          if (canNavigateBack){
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
        modifier = modifier.padding(innerPadding),
        navController = navController,
        startDestination = Screens.Categories.name
      ) {
        composable(Screens.Categories.name) {
          Column {
            Divider(color = MaterialTheme.colorScheme.secondary)
            LazyColumn() {
              items(categories) { category ->
                CategoryItem(
                  category = category,
                  onClick = {
                    selectedCategory = category.name
                    navController.navigate(Screens.Recipes.name) },
                  modifier = Modifier.fillMaxWidth())
                Divider(color = MaterialTheme.colorScheme.secondary)
              }
            }
          }
        }
        composable(Screens.Recipes.name) {
          RecipesScreen(
            recipes = RecipeRepository().loadRecipes(selectedCategory),
            onSelect = { recipe ->
              selectedRecipe = recipe
              navController.navigate(Screens.Recipe.name)
            }
          )
        }
        composable(Screens.Recipe.name){
          if(selectedRecipe != null) RecipeScreen(recipe = selectedRecipe!!)
        }
        composable(Screens.NewRecipe.name){
          NewRecipeScreen()
        }
      }
    }
  )
}

@Composable fun CategoryItem(
  category: RecipeCategory,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.clickable(onClick = onClick)) {
    Text(text = (if (category.name != "") category.name else "Muut"), modifier.padding(16.dp))
  }
}