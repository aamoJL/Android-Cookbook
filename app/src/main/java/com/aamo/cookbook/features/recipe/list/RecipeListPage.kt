package com.aamo.cookbook.features.recipe.list

import androidx.navigation.NavGraphBuilder

fun NavGraphBuilder.recipeListPages(
  onOpenRecipe: (id: Int) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenRecipeForm: () -> Unit,
  onBack: () -> Unit
) {
  recipesByCategoryScreen(
    onOpenRecipe = onOpenRecipe,
    onOpenSearch = onOpenSearch,
    onOpenRecipeForm = onOpenRecipeForm,
    onBack = onBack
  )
  recipesByBookmarkScreen(
    onOpenRecipe = onOpenRecipe,
    onOpenSearch = onOpenSearch,
    onOpenRecipeForm = onOpenRecipeForm,
    onBack = onBack
  )
  recipeSearchScreen(onOpenRecipe = onOpenRecipe, onBack = onBack)
}