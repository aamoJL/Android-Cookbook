package com.aamo.cookbook.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.utility.Tags

@Composable
fun SubCategoriesScreen(
  subCategories: List<String>,
  onSelectSubCategory: (String) -> Unit = {},
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      BasicTopAppBar(
        title = stringResource(R.string.screen_title_subcategories),
        onBack = onBack
      )
    }
  ) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
      SubCategoryList(
        categories = subCategories,
        onSelect = onSelectSubCategory
      )
    }
  }
}

@Composable
private fun SubCategoryList(categories: List<String>, onSelect: (String) -> Unit) {
  Column {
    Divider(color = MaterialTheme.colorScheme.secondary)
    LazyColumn {
      items(categories) { category ->
        SubCategoryItem(
          subCategory = category,
          onClick = { onSelect(category) },
          modifier = Modifier.fillMaxWidth()
        )
        Divider(color = MaterialTheme.colorScheme.secondary)
      }
    }
  }
}

@Composable
private fun SubCategoryItem(
  subCategory: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier
    .clickable(onClick = onClick)
    .testTag(Tags.CATEGORY_ITEM.name)) {
    Text(
      text = (if (subCategory != "") subCategory else stringResource(R.string.other_category)),
      style = MaterialTheme.typography.titleLarge,
      modifier = modifier.padding(20.dp, 40.dp, 20.dp, 40.dp)
    )
  }
}