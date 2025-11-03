package com.aamo.cookbook.features.recipe.form

import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormChapterScreen
import com.aamo.cookbook.features.recipe.form.screens.RecipeFormInfoScreen
import com.aamo.cookbook.features.recipe.form.screens.recipeFormChapterScreen
import com.aamo.cookbook.features.recipe.form.screens.recipeFormInfoScreen
import com.aamo.cookbook.features.recipe.form.use_cases.deleteRecipe
import com.aamo.cookbook.features.recipe.form.use_cases.fetchRecipe
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.utility.extensions.general.onTrue
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormPage(val id: Int)

class RecipeFormViewModel(
  private val fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients,
  private val deleteData: suspend (RecipeWithChaptersStepsAndIngredients) -> Boolean,
) : ViewModel() {
  var infoFields by mutableStateOf(RecipeFormInfoFields())
    private set

  val chapterFields = mutableListOf<RecipeFormChapterFields>()

  var isLoading by mutableStateOf(true)
    private set

  init {
    viewModelScope.launch {
      fetchData().let { (recipe, _) ->
        infoFields = RecipeFormInfoFields(
          name = recipe.name,
          category = recipe.category,
          subCategory = recipe.subCategory,
          servings = recipe.servings,
          note = recipe.note
        )
        isLoading = false
      }
    }
  }

  suspend fun deleteRecipe(): Boolean {
    return runCatching { deleteData(fetchData()) }.isSuccess
  }
}

fun NavGraphBuilder.recipeFormPage(onBack: () -> Unit, onRecipeDeleted: () -> Unit) {
  composable<RecipeFormPage> { navStack ->
    val (recipeId) = navStack.toRoute<RecipeFormPage>()
    val localContext = LocalContext.current
    val dao = RecipeDatabase.getDatabase(localContext.applicationContext).recipeDao()
    val viewmodel: RecipeFormViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeFormViewModel(
          fetchData = {
            fetchRecipe {
              if (recipeId == 0) RecipeWithChaptersStepsAndIngredients(recipe = Recipe())
              else dao.getRecipeWithChaptersStepsAndIngredients(recipeId)
                ?: throw Exception("Failed to fetch data")
            }
          },
          deleteData = {
            deleteRecipe(
              recipe = dao.getRecipe(recipeId) ?: throw Exception("Failed to fetch data"),
              deleteThumbnail = { uri ->
                IOService(localContext).deleteExternalFile(Environment.DIRECTORY_PICTURES, uri)
              }) { recipe -> dao.deleteRecipe(recipe) > 0 }
          },
        )
      }
    })

    val formNavController = rememberNavController()

    LoadingScreen(enabled = viewmodel.isLoading) {
      NavHost(navController = formNavController, startDestination = RecipeFormInfoScreen) {
        recipeFormInfoScreen(
          formData = viewmodel.infoFields,
          chapterData = viewmodel.chapterFields,
          onNewChapter = {
            formNavController.navigate(RecipeFormChapterScreen(index = null)) {
              launchSingleTop = true
            }
          },
          onDeleteRecipe = {
            viewmodel.viewModelScope.launch {
              viewmodel.deleteRecipe().onTrue { onRecipeDeleted() }
            }
          },
          onBack = onBack,
        )
        recipeFormChapterScreen(formData = {
          viewmodel.chapterFields.elementAtOrNull(
            formNavController.currentBackStackEntry?.toRoute<RecipeFormChapterScreen>()?.index ?: -1
          ) ?: RecipeFormChapterFields()
        }, stepsData = emptyList(), onNewStep = {}, onBack = {
          formNavController.navigateUp()
        })
      }
    }
  }
}