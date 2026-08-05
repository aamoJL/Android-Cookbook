package com.aamo.cookbook.features.recipe.recipe_import

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.recipe_import.use_cases.saveRecipes
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.PrimaryTopAppBar
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.general.onTrue
import com.aamo.cookbook.utility.viewmodels.SavingState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RecipeImportPage(val json: String)

class RecipeImportScreenViewModel(
  val fetchData: () -> List<RecipeWithChaptersStepsAndIngredients>,
  val saveData: suspend (List<RecipeWithChaptersStepsAndIngredients>) -> Unit,
) : ViewModel() {
  val recipes by mutableStateOf(fetchData())

  val selections = ViewModelStateList<RecipeWithChaptersStepsAndIngredients>().onChange {
    validity = checkValidity()
  }
  var savingState by mutableStateOf(SavingState())
    private set
  var validity by mutableStateOf(checkValidity())
    private set

  private fun checkValidity(): Boolean {
    if (savingState.state == SavingState.State.SAVING) return false
    return selections.values.isNotEmpty()
  }

  fun selectRecipe(vararg recipe: RecipeWithChaptersStepsAndIngredients) {
    recipe.forEach {
      if (!selections.values.contains(it)) selections.add(it)
    }
  }

  fun deselectRecipe(recipe: RecipeWithChaptersStepsAndIngredients) {
    selections.remove(recipe)
  }

  fun saveRecipes(recipes: List<RecipeWithChaptersStepsAndIngredients>) {
    viewModelScope.launch {
      saveData(recipes)
    }
  }
}

fun NavGraphBuilder.recipeImportPage(
  onSnackbar: (SnackbarProperties) -> Unit,
  onBack: () -> Unit,
) {
  composable<RecipeImportPage> { navStack ->
    val (json) = navStack.toRoute<RecipeImportPage>()
    val recipesImportedMessage = stringResource(R.string.snackbar_recipes_imported)
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: RecipeImportScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        RecipeImportScreenViewModel(
          fetchData = {
            runCatching { Json.decodeFromString<List<RecipeWithChaptersStepsAndIngredients>>(json) }.let {
              it.getOrNull() ?: emptyList()
            }
          },
          saveData = { recipes ->
            saveRecipes(dao, recipes).onTrue {
              onBack()
              onSnackbar(SnackbarProperties(message = recipesImportedMessage))
            }
          },
        )
      }
    })

    RecipeImportScreenContent(
      recipes = viewmodel.recipes,
      selections = viewmodel.selections.values,
      onBack = onBack,
      onSubmit = { viewmodel.saveRecipes(viewmodel.selections.values) },
      canSave = viewmodel.validity,
      onSelect = { viewmodel.selectRecipe(it) },
      onDeselect = { viewmodel.deselectRecipe(it) },
      onSelectAll = { viewmodel.selectRecipe(*viewmodel.recipes.toTypedArray()) })
  }
}

@Composable
fun RecipeImportScreenContent(
  recipes: List<RecipeWithChaptersStepsAndIngredients>,
  selections: List<RecipeWithChaptersStepsAndIngredients>,
  canSave: Boolean,
  onBack: () -> Unit,
  onSubmit: () -> Unit,
  onSelect: (RecipeWithChaptersStepsAndIngredients) -> Unit,
  onDeselect: (RecipeWithChaptersStepsAndIngredients) -> Unit,
  onSelectAll: () -> Unit,
) {
  Scaffold(
    topBar = {
      PrimaryTopAppBar(
        title = stringResource(R.string.title_import_recipes),
        onBack = onBack,
      ) {
        IconButton(onClick = onSelectAll) {
          Icon(
            painter = painterResource(R.drawable.select_all_24px),
            contentDescription = stringResource(R.string.cd_select_all)
          )
        }
        IconButton(onClick = onSubmit, enabled = canSave) {
          Icon(
            painter = painterResource(R.drawable.rounded_check_24),
            contentDescription = stringResource(R.string.cd_save)
          )
        }
      }
    },
  ) {
    BackgroundSurface(
      modifier = Modifier
        .padding(it)
        .fillMaxSize()
    ) {
      Surface(
        shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(8.dp)
      ) {
        LazyColumn(userScrollEnabled = true) {
          itemsIndexed(recipes) { i, recipe ->
            val selected = selections.contains(recipe)

            Column {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    if (selected) onDeselect(recipe)
                    else onSelect(recipe)
                  }) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                  Checkbox(checked = selected, onCheckedChange = null)
                  Text(text = recipe.recipe.name, fontWeight = FontWeight.Bold)
                }
              }
              if (i < recipes.size - 1) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            }
          }
        }
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview
@Composable
private fun Preview() {
  CookbookTheme {
    RecipeImportScreenContent(
      recipes = listOf(
        RecipeWithChaptersStepsAndIngredients(recipe = Recipe(name = "Recipe 1")),
        RecipeWithChaptersStepsAndIngredients(recipe = Recipe(name = "Recipe 2")),
        RecipeWithChaptersStepsAndIngredients(recipe = Recipe(name = "Recipe 3")),
        RecipeWithChaptersStepsAndIngredients(recipe = Recipe(name = "Recipe 4")),
      ),
      selections = listOf(),
      canSave = true,
      onBack = {},
      onSubmit = {},
      onSelect = {},
      onDeselect = {},
      onSelectAll = {},
    )
  }
}