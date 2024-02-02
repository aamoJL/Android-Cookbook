package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
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
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(2f)
      ) {
        Text(
          text = stringResource(R.string.app_name),
          fontFamily = Handwritten,
          style = MaterialTheme.typography.headlineLarge,
        )
      }
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
          .fillMaxWidth()
          .weight(4f)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          MainButtons(
            onSearch = onSearch,
            onAddRecipe = onAddRecipe,
            onFavorites = onFavorites,
            modifier = Modifier.padding(vertical = 32.dp)
          )
          CategoryList(
            categories = categories,
            onSelect = onSelectCategory,
          )
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
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
    modifier = modifier.defaultMinSize(minHeight = 110.dp).height(IntrinsicSize.Max)
  ) {
    MainButton(
      onClick = onSearch,
      icon = Icons.Filled.Search,
      text = stringResource(R.string.description_search),
      modifier = Modifier.weight(1f).fillMaxHeight()
    )
    MainButton(
      onClick = onFavorites,
      icon = Icons.Filled.Favorite,
      text = stringResource(R.string.button_text_favorites),
      modifier = Modifier.weight(1f).fillMaxHeight()
    )
    MainButton(
      onClick = onAddRecipe,
      icon = Icons.Filled.Add,
      text = stringResource(R.string.button_text_new),
      modifier = Modifier.weight(1f).fillMaxHeight()
    )
  }
}

@Composable
private fun CategoryList(
  categories: List<String>,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
    Text(
      text = stringResource(R.string.screen_title_categories),
      fontFamily = Handwritten,
      style = MaterialTheme.typography.headlineMedium
    )
    LazyColumn {
      items(categories) { category ->
        ElevatedButton(
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
          ),
          onClick = { onSelect(category) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag(Tags.CATEGORY_ITEM.name)
        ) {
          Text(
            text = category,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .padding(8.dp)
              .fillMaxWidth()
          )
        }
      }
    }
  }
}


