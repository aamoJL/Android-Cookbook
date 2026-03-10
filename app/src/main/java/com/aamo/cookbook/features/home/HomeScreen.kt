package com.aamo.cookbook.features.home

import android.app.UiModeManager
import android.os.Build
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
import com.aamo.cookbook.features.home.use_cases.fetchRecipeCategoriesFlow
import com.aamo.cookbook.ui.components.BackgroundSurface
import com.aamo.cookbook.ui.components.HorizontalDividerLabel
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.extensions.general.ifElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen

class HomeScreenViewModel(fetchCategories: () -> Flow<List<String>>) : ViewModel() {
  val categories = fetchCategories().stateIn(
    scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null
  )
}

fun NavGraphBuilder.homeScreen(
  onOpenSearch: () -> Unit,
  onOpenRecipeForm: () -> Unit,
  onOpenBookmarks: () -> Unit,
  onOpenRecipesByCategory: (category: String) -> Unit
) {
  composable<HomeScreen> {
    val dao = RecipeDatabase.getDatabase(LocalContext.current.applicationContext).recipeDao()
    val viewmodel: HomeScreenViewModel = viewModel(factory = viewModelFactory {
      initializer {
        HomeScreenViewModel(fetchCategories = { fetchRecipeCategoriesFlow(dao) })
      }
    })
    val categories by viewmodel.categories.collectAsStateWithLifecycle()

    LoadingScreen(loading = categories == null) {
      HomeScreenContent(
        categories = checkNotNull(categories),
        onSearch = onOpenSearch,
        onNewRecipe = onOpenRecipeForm,
        onBookmarks = onOpenBookmarks,
        onSelectCategory = onOpenRecipesByCategory
      )
    }
  }
}

@Composable
private fun HomeScreenContent(
  categories: List<String>,
  onSearch: () -> Unit,
  onNewRecipe: () -> Unit,
  onBookmarks: () -> Unit,
  onSelectCategory: (String) -> Unit,
) {
  val context = LocalContext.current
  val configuration = LocalConfiguration.current

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
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
              contentAlignment = Alignment.TopEnd, modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
            ) {
              IconButton(
                onClick = {
                  context.getSystemService<UiModeManager>()?.also { manager ->
                    if (configuration.isNightModeActive) manager.setApplicationNightMode(
                      UiModeManager.MODE_NIGHT_NO
                    )
                    else manager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                  }
                }) {
                Icon(
                  painter = ifElse(
                    condition = configuration.isNightModeActive,
                    ifTrue = { painterResource(R.drawable.rounded_light_mode_24) },
                    ifFalse = { painterResource(R.drawable.dark_mode_24px) }),
                  contentDescription = stringResource(R.string.cd_change_app_theme)
                )
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
    )
  }
}