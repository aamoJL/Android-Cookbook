package com.aamo.cookbook.features.home

import android.app.UiModeManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aamo.cookbook.R
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.home.use_cases.fetchAllRecipes
import com.aamo.cookbook.features.home.use_cases.fetchRecipeCategoriesFlow
import com.aamo.cookbook.features.home.use_cases.loadFromFile
import com.aamo.cookbook.features.home.use_cases.saveToFile
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.SnackbarProperties
import com.aamo.cookbook.utility.extensions.asNew
import com.aamo.cookbook.utility.extensions.general.ifElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
object HomeScreen

class HomeScreenViewModel(
  fetchCategories: () -> Flow<List<String>>,
  val fetchCompleteRecipes: suspend () -> List<RecipeWithChaptersStepsAndIngredients>,
  val exportRecipes: (uri: Uri, json: String) -> Unit,
  val importRecipes: (uri: Uri) -> String?,
) : ViewModel() {
  val categories = fetchCategories().stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )

  fun exportRecipes(uri: Uri) {
    viewModelScope.launch {
      val json = fetchCompleteRecipes().map { it.asNew() }.let { recipes ->
        Json.encodeToString(recipes)
      }
      exportRecipes(uri, json)
    }
  }

  fun importRecipes(uri: Uri): String? {
    return importRecipes.invoke(uri)
  }
}

fun NavGraphBuilder.homeScreen(
  onOpenSearch: () -> Unit,
  onOpenRecipeForm: () -> Unit,
  onOpenBookmarks: () -> Unit,
  onOpenRecipesByCategory: (category: String) -> Unit,
  onOpenImport: (json: String) -> Unit,
  onSnackbar: (SnackbarProperties) -> Unit
) {
  composable<HomeScreen> {
    val recipesExportedMessage = stringResource(R.string.snackbar_recipes_exported)
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val dao = RecipeDatabase.getDatabase(context.applicationContext).recipeDao()
    val viewmodel: HomeScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        HomeScreenViewModel(
          fetchCategories = { fetchRecipeCategoriesFlow(dao) },
          fetchCompleteRecipes = { fetchAllRecipes(dao) },
          exportRecipes = { uri, json -> saveToFile(context.contentResolver, uri, json) },
          importRecipes = { uri ->
            runCatching { loadFromFile(context.contentResolver, uri) }.getOrNull()
          })
      }
    })
    val categories by viewmodel.categories.collectAsStateWithLifecycle()

    val exportLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        it?.also { uri ->
          viewmodel.exportRecipes(uri)
          onSnackbar(SnackbarProperties(message = recipesExportedMessage))
        }
      }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
      it?.also { uri ->
        viewmodel.importRecipes(uri)?.also { result ->
          onOpenImport(result) // Redirect will crash if done in viewmodel
        }
      }
    }

    LoadingScreen(loading = categories == null) {
      HomeScreenContent(
        categories = checkNotNull(categories),
        isNightMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) configuration.isNightModeActive else null,
        onSearch = onOpenSearch,
        onNewRecipe = onOpenRecipeForm,
        onBookmarks = onOpenBookmarks,
        onSelectCategory = onOpenRecipesByCategory,
        onExportRecipes = { exportLauncher.launch("recipes.json") },
        onImportRecipes = { importLauncher.launch("application/json") },
        onChangeTheme = {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService<UiModeManager>()?.also { manager ->
              if (configuration.isNightModeActive) manager.setApplicationNightMode(
                UiModeManager.MODE_NIGHT_NO
              )
              else manager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
            }
          }
        })
    }
  }
}

@Composable
private fun HomeScreenContent(
  categories: List<String>,
  isNightMode: Boolean?,
  onSearch: () -> Unit,
  onNewRecipe: () -> Unit,
  onBookmarks: () -> Unit,
  onSelectCategory: (String) -> Unit,
  onExportRecipes: () -> Unit,
  onImportRecipes: () -> Unit,
  onChangeTheme: () -> Unit,
) {
  var menuExpanded by remember { mutableStateOf(false) }

  BackgroundSurface(modifier = Modifier.fillMaxSize()) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
      Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
          .fillMaxHeight(.3f)
          .fillMaxWidth(),
        shadowElevation = 4.dp
      ) {
        Box(
          modifier = Modifier
            .padding(
              top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
            )
            .fillMaxSize()
        ) {

          Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
              .fillMaxWidth()
              .padding(4.dp),
          ) {
            Box {
              IconButton(onClick = { menuExpanded = true }) {
                Icon(
                  painter = painterResource(R.drawable.rounded_more_vert_24),
                  contentDescription = stringResource(R.string.cd_open_options)
                )
              }
              DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.btn_export_recipes)) },
                  onClick = {
                    onExportRecipes()
                    menuExpanded = false
                  },
                  leadingIcon = {
                    Icon(
                      painter = painterResource(R.drawable.file_export_24px),
                      contentDescription = stringResource(R.string.btn_export_recipes),
                    )
                  },
                )
                DropdownMenuItem(
                  text = { Text(stringResource(R.string.btn_import_recipes)) },
                  onClick = onImportRecipes,
                  leadingIcon = {
                    Icon(
                      painter = painterResource(R.drawable.file_open_24px),
                      contentDescription = stringResource(R.string.btn_export_recipes),
                    )
                  },
                )
                if (isNightMode != null) {
                  HorizontalDivider()
                  DropdownMenuItem(
                    text = { Text(stringResource(R.string.btn_change_theme)) },
                    onClick = onChangeTheme,
                    leadingIcon = {
                      Icon(
                        painter = ifElse(
                          condition = isNightMode,
                          ifTrue = { painterResource(R.drawable.rounded_light_mode_24) },
                          ifFalse = { painterResource(R.drawable.dark_mode_24px) }),
                        contentDescription = stringResource(R.string.cd_change_app_theme)
                      )
                    },
                  )
                }
              }
            }
          }
          Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
              text = stringResource(R.string.app_name),
              fontFamily = Handwritten,
              autoSize = TextAutoSize.StepBased(),
              softWrap = false,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.displayLarge,
              modifier = Modifier.padding(horizontal = 32.dp)
            )
          }
        }
      }
      MainButtons(
        onSearch = onSearch,
        onNewRecipe = onNewRecipe,
        onBookmarks = onBookmarks,
        modifier = Modifier
      )
      CategoryList(
        categories = categories,
        onSelect = onSelectCategory,
        modifier = Modifier.widthIn(max = 500.dp)
      )
    }
  }
}

@Composable
private fun MainButtons(
  onSearch: () -> Unit,
  onNewRecipe: () -> Unit,
  onBookmarks: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
      MainIconButton(
        onClick = onSearch,
        icon = painterResource(R.drawable.rounded_search_24),
        text = stringResource(R.string.btn_search),
      )
      MainIconButton(
        onClick = onBookmarks,
        icon = painterResource(R.drawable.rounded_bookmark_24),
        text = stringResource(R.string.btn_bookmarks),
      )
      MainIconButton(
        onClick = onNewRecipe,
        icon = painterResource(R.drawable.rounded_add_24),
        text = stringResource(R.string.btn_new),
      )
    }
  }
}

@Composable
fun MainIconButton(
  onClick: () -> Unit, icon: Painter, text: String, modifier: Modifier = Modifier
) {
  FilledIconButton(
    onClick = onClick,
    colors = IconButtonDefaults.filledIconButtonColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ),
    modifier = modifier
      .size(60.dp)
      .shadow(elevation = 2.dp, CircleShape),
  ) {
    Icon(painter = icon, contentDescription = text)
  }
}

@Composable
private fun CategoryList(
  categories: List<String>, onSelect: (String) -> Unit, modifier: Modifier = Modifier
) {
  Box(contentAlignment = Alignment.Center, modifier = modifier) {
    Surface(
      shadowElevation = 2.dp, shape = RoundedCornerShape(10.dp),
      border = BorderStroke(
        width = 1.dp, color = MaterialTheme.colorScheme.inversePrimary.copy(alpha = .2f)
      ),
      modifier = Modifier
        .padding(horizontal = 64.dp)
        .padding(bottom = 16.dp),
    ) {
      Column {
        HorizontalDividerLabel(
          label = stringResource(R.string.text_menu),
          color = MaterialTheme.colorScheme.inversePrimary,
          style = MaterialTheme.typography.titleLarge,
          fontFamily = Handwritten,
          modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp),
        )
        LazyColumn(
          userScrollEnabled = true, modifier = Modifier.padding(32.dp, 0.dp, 32.dp, 16.dp)
        ) {
          items(categories) { category ->
            TextButton(
              colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
              ),
              shape = RectangleShape,
              onClick = { onSelect(category) },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = category, textAlign = TextAlign.Center)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = .25f))
          }
        }
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
  CookbookTheme(useDarkTheme = true) {
    HomeScreenContent(
      categories = listOf("Category 1", "Category 2", "Category 3", "Category 4"),
      onSearch = {},
      onNewRecipe = {},
      onBookmarks = {},
      onSelectCategory = {},
      isNightMode = false,
      onExportRecipes = {},
      onImportRecipes = {},
      onChangeTheme = {},
    )
  }
}