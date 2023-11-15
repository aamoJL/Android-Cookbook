package com.aamo.cookbook.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aamo.cookbook.model.Recipe

@Composable fun RecipeScreen(recipe: Recipe, modifier: Modifier = Modifier.fillMaxSize()) {
  Box(modifier = modifier){
    Text(text = recipe.name)
  }
}