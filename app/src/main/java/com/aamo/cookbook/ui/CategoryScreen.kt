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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeCategory
import com.aamo.cookbook.repository.RecipeCategoryRepository
import com.aamo.cookbook.repository.RecipeRepository
import com.aamo.cookbook.ui.theme.RecipeScreen

enum class Screens() {
  Categories,
  Recipes,
  Recipe,
  NewRecipe,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun CookBookApp(modifier: Modifier = Modifier) {
  val categories = remember { RecipeCategoryRepository().loadCategories() }
  val navController = rememberNavController()
  val currentRoute = navController
    .currentBackStackEntryFlow
    .collectAsState(initial = navController.currentBackStackEntry).value?.destination?.route

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Text(
            "Cookbook",
            style = MaterialTheme.typography.displayLarge
          )
        })
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
                  onClick = { navController.navigate(Screens.Recipes.name) },
                  modifier = Modifier.fillMaxWidth())
                Divider(color = MaterialTheme.colorScheme.secondary)
              }
            }
          }
        }
        composable(Screens.Recipes.name) {
          RecipesScreen(
            recipes = RecipeRepository().loadRecipes(),
            onSelect = { _ ->
              navController.navigate(Screens.Recipe.name)
            }
          )
        }
        composable(Screens.Recipe.name){
          RecipeScreen(recipe = Recipe("asd"))
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
    Text(text = category.name, modifier.padding(16.dp))
  }
}