package com.aamo.cookbook.utility

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Defines a custom [ActivityResultRegistry] to be used when calls to ActivityResultContracts API via rememberLauncherForActivityResult.
 *
 * This allow us to mock external Activity calls in tests or in mock mode.
 */
@Suppress("TestFunctionName")
@Composable
fun WithActivityResultRegistry(activityResultRegistry: ActivityResultRegistry, content: @Composable () -> Unit) {
  val activityResultRegistryOwner = object : ActivityResultRegistryOwner {
    override val activityResultRegistry = activityResultRegistry
  }
  CompositionLocalProvider(LocalActivityResultRegistryOwner provides activityResultRegistryOwner) { content() }
}