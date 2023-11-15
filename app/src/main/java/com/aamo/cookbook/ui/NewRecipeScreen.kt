package com.aamo.cookbook.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NewRecipeScreen(modifier: Modifier = Modifier.fillMaxSize()) {
  Box(modifier = modifier){
    Text(text = "New recipe")
  }
}