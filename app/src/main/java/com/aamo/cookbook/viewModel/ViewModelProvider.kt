package com.aamo.cookbook.viewModel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aamo.cookbook.CookbookApplication

object ViewModelProvider {
  val Factory = viewModelFactory {
    initializer {
      AppViewModel(cookbookApplication().container.recipeRepository)
    }
    initializer {
      RecipeScreenViewModel(
        cookbookApplication().container.recipeRepository,
        createSavedStateHandle()
      )
        .apply { init() }
    }
    initializer {
      EditRecipeViewModel(
        cookbookApplication().container.recipeRepository,
        createSavedStateHandle()
      )
        .apply { init() }
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