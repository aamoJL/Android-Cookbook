package com.aamo.cookbook.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
import com.aamo.cookbook.ui.components.LoadingScreen
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
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
  Surface(color = MaterialTheme.colorScheme.primary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        contentAlignment = Alignment.Center, modifier = Modifier.weight(2f)
      ) {
        Text(
          text = stringResource(R.string.app_name),
          fontFamily = Handwritten,
          style = MaterialTheme.typography.headlineLarge,
        )
      }
      Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
          .fillMaxWidth()
          .weight(5f)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        ) {
          MainButtons(
            onSearch = onSearch,
            onNewRecipe = onNewRecipe,
            onBookmarks = onBookmarks,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 36.dp)
          )
          Text(
            text = stringResource(R.string.text_choose_category),
            fontFamily = Handwritten,
            style = MaterialTheme.typography.headlineMedium
          )
          ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .weight(1f)
              .padding(8.dp)
              .fillMaxWidth()
          ) {
            CategoryList(
              categories = categories,
              onSelect = onSelectCategory,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
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
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier.then(
      Modifier
        .defaultMinSize(minHeight = 110.dp)
        .height(IntrinsicSize.Max)
    )
  ) {
    MainButton(
      onClick = onSearch,
      icon = painterResource(R.drawable.rounded_search_24),
      text = stringResource(R.string.btn_search),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
    MainButton(
      onClick = onBookmarks,
      icon = painterResource(R.drawable.rounded_bookmark_24),
      text = stringResource(R.string.btn_bookmarks),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
    MainButton(
      onClick = onNewRecipe,
      icon = painterResource(R.drawable.rounded_add_24),
      text = stringResource(R.string.btn_new),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
  }
}

@Composable
private fun MainButton(
  onClick: () -> Unit,
  icon: Painter,
  text: String,
  modifier: Modifier = Modifier,
  buttonColors: ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.secondaryContainer,
    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
  ),
) {
  ElevatedButton(
    onClick = onClick,
    shape = RoundedCornerShape(8.dp),
    colors = buttonColors,
    contentPadding = PaddingValues(0.dp),
    modifier = modifier
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(painter = icon, contentDescription = null)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = text, softWrap = false)
    }
  }
}

@Composable
private fun CategoryList(
  categories: List<String>, onSelect: (String) -> Unit, modifier: Modifier = Modifier
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = true, modifier = modifier
  ) {
    items(categories) { category ->
      ElevatedButton(
        shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.elevatedButtonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ), onClick = { onSelect(category) }, modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = category,
          textAlign = TextAlign.Center,
          modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
        )
      }
    }
  }
}

@Suppress("HardCodedStringLiteral")
@PreviewLightDark
@Composable
private fun Preview() {
  CookbookTheme {
    HomeScreenContent(
      categories = listOf("Category 1", "Category 2"),
      onSearch = {},
      onNewRecipe = {},
      onBookmarks = {},
      onSelectCategory = {})
  }
}