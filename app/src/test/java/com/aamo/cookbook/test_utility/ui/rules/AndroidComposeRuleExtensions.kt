package com.aamo.cookbook.test_utility.ui.rules

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import com.aamo.cookbook.utility.tags.UITag
import kotlinx.coroutines.yield

suspend fun PageTest.waitForLoading() {
  val progressElement = rule.onNodeWithTag(UITag.PROGRESS_INDICATOR.name)

  while (progressElement.isDisplayed()) yield()
}

suspend fun SemanticsNodeInteraction.waitForDisplayed(): SemanticsNodeInteraction {
  while (this.isNotDisplayed()) yield()

  return this
}

fun SemanticsNodeInteraction.performClickWithKeyboard(): SemanticsNodeInteraction {
  @OptIn(ExperimentalTestApi::class) return tryPerformAccessibilityChecks().requestFocus()
    .performKeyInput {
      keyDown(Key.Enter)
      keyUp(Key.Enter)
    }
}