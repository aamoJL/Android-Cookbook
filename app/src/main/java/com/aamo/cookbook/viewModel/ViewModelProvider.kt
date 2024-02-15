package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aamo.cookbook.CookbookApplication
import com.aamo.cookbook.Screen

object ViewModelProvider {
  val Factory = viewModelFactory {
    initializer {
      AppViewModel(cookbookApplication().container.recipeRepository)
    }
    initializer {
      RecipeScreenViewModel(
        recipeRepository = cookbookApplication().container.recipeRepository,
        ioService = cookbookApplication().container.ioService
      ).apply { init(recipeId = createSavedStateHandle()[Screen.Recipe.argumentName] ?: 0) }
    }
    initializer {
      EditRecipeViewModel(cookbookApplication().container.recipeRepository)
        .apply { init(recipeId = createSavedStateHandle()[Screen.Recipe.argumentName] ?: 0) }
    }
    initializer {
      RecipeSearchViewModel(
        cookbookApplication().container.recipeRepository
      )
    }
  }
}

/**
 * Extension function to queries for Application object and returns an instance of
 * [CookbookApplication].
 */
fun CreationExtras.cookbookApplication(): CookbookApplication =
  (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CookbookApplication)