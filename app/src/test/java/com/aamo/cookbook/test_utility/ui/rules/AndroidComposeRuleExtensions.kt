package com.aamo.cookbook.test_utility.ui.rules

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
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