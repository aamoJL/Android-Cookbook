package com.aamo.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aamo.cookbook.features.home.HomePage
import com.aamo.cookbook.ui.theme.CookbookTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      CookbookTheme {
        MainContent()
      }
    }
  }
}

@Composable
fun MainContent() {
  val snackState = remember { SnackbarHostState() }
  val snackScope = rememberCoroutineScope()

  Box {
    HomePage(
      onShowSnackbar = { properties ->
        snackScope.launch {
          snackState.showSnackbar(
            message = properties.message,
            actionLabel = properties.actionLabel,
            withDismissAction = properties.withDismissAction,
            duration = properties.duration
          )
        }
      })
    SnackbarHost(hostState = snackState, Modifier.align(Alignment.BottomCenter))
  }
}