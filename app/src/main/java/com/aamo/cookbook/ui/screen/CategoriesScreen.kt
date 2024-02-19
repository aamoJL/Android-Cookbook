package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.ui.theme.Handwritten
import com.aamo.cookbook.utility.Tags

@Composable
fun CategoriesScreen(
  categories: List<String>,
  onSelectCategory: (String) -> Unit = {},
  onAddRecipe: () -> Unit = {},
  onSearch: () -> Unit = {},
  onFavorites: () -> Unit = {}
) {
  Surface(color = MaterialTheme.colorScheme.primary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.weight(2f)
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
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
        ) {
          MainButtons(
            onSearch = onSearch,
            onAddRecipe = onAddRecipe,
            onFavorites = onFavorites,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 36.dp)
          )
          Text(
            text = stringResource(R.string.screen_title_categories),
            fontFamily = Handwritten,
            style = MaterialTheme.typography.headlineMedium
          )
          ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .weight(1f)
              .padding(8.dp)
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
fun MainButton(
  onClick: () -> Unit,
  icon: ImageVector,
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
    modifier = modifier
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(imageVector = icon, contentDescription = null)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = text)
    }
  }
}

@Composable
fun MainButtons(
  onSearch: () -> Unit,
  onAddRecipe: () -> Unit,
  onFavorites: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.then(
      Modifier
        .defaultMinSize(minHeight = 110.dp)
        .height(IntrinsicSize.Max)
    )
  ) {
    MainButton(
      onClick = onSearch,
      icon = Icons.Filled.Search,
      text = stringResource(R.string.description_search),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
    MainButton(
      onClick = onFavorites,
      icon = Icons.Filled.Favorite,
      text = stringResource(R.string.button_text_favorites),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
    MainButton(
      onClick = onAddRecipe,
      icon = Icons.Filled.Add,
      text = stringResource(R.string.button_text_new),
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight()
    )
  }
}

@Composable
private fun CategoryList(
  categories: List<String>,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    userScrollEnabled = true,
    modifier = modifier,
  ) {
    items(categories) { category ->
      ElevatedButton(
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        onClick = { onSelect(category) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag(Tags.CATEGORY_ITEM.name)
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

@PreviewLightDark
@Composable
private fun Preview(){
  CookbookTheme {
    CategoriesScreen(categories = listOf(
      "Category 1", "Category 2"
    ))
  }
}


