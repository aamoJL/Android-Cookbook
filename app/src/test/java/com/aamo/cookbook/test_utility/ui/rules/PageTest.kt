package com.aamo.cookbook.test_utility.ui.rules

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.aamo.cookbook.MainActivity
import org.junit.Rule

open class PageTest {
  @get:Rule val rule = createAndroidComposeRule<MainActivity>()
}