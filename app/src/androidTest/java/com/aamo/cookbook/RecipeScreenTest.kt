package com.aamo.cookbook

class RecipeScreenTest {
//  private lateinit var navController: TestNavHostController
//  private lateinit var viewModel: AppViewModel
//
//  private val categoryIndex = 0
//  private val recipeIndex = 0
//
//  @get:Rule
//  val rule = createAndroidComposeRule<ComponentActivity>()
//
//  @Before
//  fun setupNavHost() {
//    rule.setContent {
//      viewModel = viewModel(factory = ViewModelProvider.Factory)
//      navController = TestNavHostController(LocalContext.current)
//      navController.navigatorProvider.addNavigator(ComposeNavigator())
//      CookbookTheme {
//        MainNavGraph(
//          navController = navController,
//          viewModel = viewModel
//        )
//      }
//    }
//
//    Navigation(rule).navigateTo_RecipeScreen(categoryIndex, recipeIndex)
//  }
//
//  @Test
//  fun pageTitle_equals() = runTest {
//    val expected = viewModel.getRecipesByCategory(viewModel.selectedCategory.value).first().elementAt(recipeIndex).name
//    rule.onNodeWithTag(Tags.SCREEN_TITLE.name).assertTextContains(expected)
//  }
//
//  @Test
//  fun backButton_isVisible() {
//    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
//  }
//
//  @Test
//  fun onBack() {
//    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()
//
//    navController.assertCurrentRouteName(Screen.Recipes.getRoute())
//  }
//
//  @Test
//  fun onPagerSwipe() = runTest {
//    rule.onNodeWithTag(Tags.PAGER.name)
//      .performTouchInput { swipeLeft() }
//
//    rule.onNodeWithText("1. ${selectedRecipe.chapters.elementAt(0).name}").assertExists()
//  }
//
//  @Test
//  fun onProgress_incomplete() {
//    val chapterCount = selectedRecipe.chapters.size
//
//    // Swipe to next page
//    rule.onNodeWithTag(Tags.PAGER.name)
//      .performTouchInput { swipeLeft() }
//
//    // Check only one checkbox
//    rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name)[0].performClick()
//
//    // Swipe to last page
//    repeat(chapterCount){
//      rule.onNodeWithTag(Tags.PAGER.name)
//        .performTouchInput { swipeLeft() }
//    }
//
//    rule.onNodeWithText("Valmis!").assertDoesNotExist()
//  }
//
//  @Test
//  fun onProgress_completed() {
//    val chapterCount = selectedRecipe.chapters.size
//
//    repeat(chapterCount){
//      // Swipe to next page
//      rule.onNodeWithTag(Tags.PAGER.name)
//        .performTouchInput { swipeLeft() }
//
//      // Check every progress checkbox
//      rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name).apply {
//        fetchSemanticsNodes().forEachIndexed { index, _ ->
//          get(index).performClick()
//        }
//      }
//    }
//
//    // Swipe to last page
//    rule.onNodeWithTag(Tags.PAGER.name)
//      .performTouchInput { swipeLeft() }
//
//    rule.onNodeWithText("Valmis!").assertExists()
//  }
}