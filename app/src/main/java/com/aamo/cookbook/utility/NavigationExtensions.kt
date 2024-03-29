package com.aamo.cookbook.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.aamo.cookbook.viewModel.ViewModelProvider

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
  val navGraphRoute = destination.parent?.route ?: return viewModel(factory = ViewModelProvider.Factory)
  val parentEntry = remember(this) {
    navController.getBackStackEntry(navGraphRoute)
  }

  return viewModel(parentEntry, factory = ViewModelProvider.Factory)
}