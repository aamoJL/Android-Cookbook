package com.aamo.cookbook

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.assertCurrentRouteName
import com.aamo.cookbook.viewModel.AppViewModel
import com.aamo.cookbook.viewModel.ViewModelProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
  private lateinit var navController: TestNavHostController
  private lateinit var viewModel: AppViewModel

  @get:Rule
  val rule = createComposeRule()

  @Before
  fun setupNavHost(){
    rule.setContent {
      viewModel = viewModel(factory = ViewModelProvider.Factory)
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      CookbookTheme {
        MainNavGraph(
          navController = navController,
          appViewModel = viewModel
        )
      }
    }
  }

  @Test
  fun verifyStartDestination(){
    navController.assertCurrentRouteName(Screen.Categories.getRoute())
  }
}

