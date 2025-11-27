package com.aamo.cookbook.features.recipe.form

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreen
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.saveRecipe
import com.aamo.cookbook.service.PhotoService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.onTrue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormPage(val id: Long)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val saveData: suspend (fields: RecipeFormInfoFields, id: Long, thumbnail: String) -> Unit,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Unit,
) : ViewModel() {
  var recipe = flow { emit(fetchData()) }.stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  fun deleteRecipe() {
    viewModelScope.launch {
      runCatching { deleteData(checkNotNull(recipe.value)) }
    }
  }

  fun saveRecipe(data: RecipeFormInfoFields) {
    viewModelScope.launch {
      runCatching {
        val recipe = checkNotNull(recipe.value).recipe
        saveData(data, recipe.id, recipe.thumbnailUri)
      }
    }
  }
}

fun NavGraphBuilder.recipeFormPage(
  onOpenRecipe: (id: Long) -> Unit,
  onOpenCategories: () -> Unit,
  onSnackbar: (SnackbarProperties) -> Unit,
  onBack: () -> Unit
) {
  composable<RecipeFormPage> { navStack ->
    val (recipeId) = navStack.toRoute<RecipeFormPage>()
    val localContext = LocalContext.current
    val recipeDeletedSnackbarMessage = stringResource(R.string.snackbar_recipe_deleted_successfully)
    val dao = RecipeDatabase.getDatabase(localContext.applicationContext).recipeDao()
    val viewmodel: RecipeFormViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormViewModel(
          fetchData = { fetchRecipe(dao = dao, recipeId = recipeId) },
          deleteData = { recipe ->
            deleteRecipe(
              dao = dao, photoService = PhotoService(context = localContext), recipe = recipe.recipe
            ).onTrue {
              onSnackbar(SnackbarProperties(recipeDeletedSnackbarMessage))
              onOpenCategories()
            }
          },
          saveData = { fields, id, thumbnail ->
            saveRecipe(
              dao = dao, id = id, thumbnailUri = thumbnail, fields = fields
            ).also { result -> onOpenRecipe(result) }
          },
        )
      }
    })

    val recipe = viewmodel.recipe.collectAsStateWithLifecycle().value

    LoadingScreen(loading = recipe == null) {
      RecipeFormInfoScreen(
        recipe = checkNotNull(recipe),
        onSubmit = { viewmodel.saveRecipe(it) },
        onDeleteRecipe = { viewmodel.deleteRecipe() },
        onBack = onBack
      )
    }
  }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.primaryEnterTransition(): EnterTransition {
  return fadeIn(
    animationSpec = tween(300, easing = LinearEasing)
  ) + slideIntoContainer(
    animationSpec = tween(300, easing = EaseIn),
    towards = AnimatedContentTransitionScope.SlideDirection.Start
  )
}

fun secondaryEnterTransition(): EnterTransition {
  return fadeIn(animationSpec = tween(300, easing = LinearEasing)) + scaleIn(
    animationSpec = tween(300, easing = EaseIn), initialScale = 0.9f
  )
}

fun primaryExitTransition(): ExitTransition {
  return fadeOut(animationSpec = tween(300, easing = LinearEasing)) + scaleOut(
    animationSpec = tween(300, easing = EaseOut), targetScale = 0.9f
  )
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.secondaryExitTransition(): ExitTransition {
  return fadeOut(animationSpec = tween(300, easing = LinearEasing)) + slideOutOfContainer(
    animationSpec = tween(300, easing = EaseOut),
    towards = AnimatedContentTransitionScope.SlideDirection.End
  )
}