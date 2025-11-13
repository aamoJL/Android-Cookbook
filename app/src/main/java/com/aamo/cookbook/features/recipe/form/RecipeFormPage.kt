package com.aamo.cookbook.features.recipe.form

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreen
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fromDao
import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.toDao
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.utility.extensions.general.onNotNull
import com.aamo.cookbook.utility.extensions.general.onTrue
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormPage(val id: Long)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val saveData: suspend (RecipeWithChaptersStepsAndIngredients) -> Long?,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Boolean,
) : ViewModel() {
  var recipe by mutableStateOf(RecipeWithChaptersStepsAndIngredients(recipe = Recipe()))
    private set

  var isLoading by mutableStateOf(true)
    private set

  init {
    viewModelScope.launch {
      fetchData().let { result ->
        recipe = result
        isLoading = false
      }
    }
  }

  suspend fun deleteRecipe(): Boolean {
    return runCatching { deleteData(recipe) }.getOrDefault(defaultValue = false)
  }

  suspend fun saveRecipe(data: RecipeFormInfoFields): Long? {
    return runCatching {
      saveData(data.toDao(id = recipe.recipe.id, thumbnailUri = recipe.recipe.thumbnailUri))
    }.getOrNull()
  }
}

fun NavGraphBuilder.recipeFormPage(
  onBack: () -> Unit, onOpenRecipe: (id: Long) -> Unit, onOpenCategories: () -> Unit
) {
  composable<RecipeFormPage> { navStack ->
    val (recipeId) = navStack.toRoute<RecipeFormPage>()
    val localContext = LocalContext.current
    val dao = RecipeDatabase.getDatabase(localContext.applicationContext).recipeDao()
    val viewmodel: RecipeFormViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormViewModel(
          fetchData = {
            fetchRecipe {
              if (recipeId == 0L) RecipeWithChaptersStepsAndIngredients(recipe = Recipe())
              else dao.getCompleteRecipe(recipeId) ?: throw Exception("Failed to fetch data")
            }
          },
          deleteData = {
            deleteRecipe(
              recipe = dao.getRecipe(recipeId) ?: throw Exception("Failed to fetch data"),
              deleteThumbnail = { uri ->
                IOService(localContext).deleteExternalFile(Environment.DIRECTORY_PICTURES, uri)
              }) { recipe -> dao.delete(recipe) > 0 }
          },
          saveData = { entity ->
            saveRecipe(recipe = entity) {
              dao.upsert(it)
            }
          },
        )
      }
    })

    LoadingScreen(viewmodel.isLoading) {
      RecipeFormInfoScreen(
        formData = remember { mutableStateOf(RecipeFormInfoFields.fromDao(dao = viewmodel.recipe)).value },
        onSubmit = {
          viewmodel.viewModelScope.launch {
            viewmodel.saveRecipe(it).onNotNull { id -> onOpenRecipe(id) }
          }
        },
        onDeleteRecipe = {
          viewmodel.viewModelScope.launch {
            viewmodel.deleteRecipe().onTrue { onOpenCategories() }
          }
        },
        onBack = onBack
      )
    }
  }
}

// TODO: transitions
//fun AnimatedContentTransitionScope<NavBackStackEntry>.primaryEnterTransition(): EnterTransition {
//  return fadeIn(
//    animationSpec = tween(300, easing = LinearEasing)
//  ) + slideIntoContainer(
//    animationSpec = tween(300, easing = EaseIn),
//    towards = AnimatedContentTransitionScope.SlideDirection.Start
//  )
//}
//
//fun secondaryEnterTransition(): EnterTransition {
//  return fadeIn(
//    animationSpec = tween(300, easing = LinearEasing)
//  ) + scaleIn(
//    animationSpec = tween(300, easing = EaseIn), initialScale = 0.9f
//  )
//}
//
//fun primaryExitTransition(): ExitTransition {
//  return fadeOut(
//    animationSpec = tween(300, easing = LinearEasing)
//  ) + scaleOut(
//    animationSpec = tween(300, easing = EaseOut), targetScale = 0.9f
//  )
//}
//
//fun AnimatedContentTransitionScope<NavBackStackEntry>.secondaryExitTransition(): ExitTransition {
//  return fadeOut(
//    animationSpec = tween(300, easing = LinearEasing)
//  ) + slideOutOfContainer(
//    animationSpec = tween(300, easing = EaseOut),
//    towards = AnimatedContentTransitionScope.SlideDirection.End
//  )
//}