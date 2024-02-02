package com.aamo.cookbook.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.R
import com.aamo.cookbook.ui.screen.recipeScreen.RecipeScreenContent
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeScreenTest {
  private val recipe = Mocker.mockRecipeList().first()
  private var chapterUiStates by mutableStateOf<List<RecipeScreenViewModel.ChapterPageUiState>>(emptyList())
  private var servingsState by mutableStateOf(RecipeScreenViewModel.ServingsState(recipe.value.servings, recipe.value.servings))
  private var favoriteState by mutableStateOf(false)
  private var wasProgressChanged: Boolean = false
  private var wasClicked: Boolean = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      chapterUiStates = recipe.chapters.map {
        RecipeScreenViewModel.ChapterPageUiState.fromChapter(it) }

      CookbookTheme {
        RecipeScreenContent(
          summaryPageUiState = RecipeScreenViewModel.SummaryPageUiState(
            recipeName = recipe.value.name,
            chaptersWithIngredients = recipe.chapters.map { chapter ->
              Pair(chapter.value.name, chapter.steps.flatMap { it.ingredients })
            }
          ),
          chapterUiStates = chapterUiStates,
          servingsState = servingsState,
          favoriteState = favoriteState,
          onProgressChange = { _, _, _ ->
            wasProgressChanged = true
          },
          onBack = { wasClicked = true },
          onEditRecipe = { wasClicked = true },
          onFavoriteChange = { favoriteState = it },
          onServingsCountChange = { servingsState = servingsState.copy(current = it) }
        )
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    val expected = recipe.value.name
    rule.onNodeWithTag(Tags.SCREEN_TITLE.name).assertTextContains(expected)
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    assert(wasClicked)
  }

  @Test
  fun onPagerSwipe() {
    rule.onNodeWithTag(Tags.PAGER.name).performTouchInput { swipeLeft() }

    rule.onNodeWithText("1. ${chapterUiStates.first().chapter.value.name}").assertExists()
  }

  @Test
  fun changeProgress() = runTest {
    rule.onNodeWithTag(Tags.PAGER.name)
      .performTouchInput { swipeLeft() }

    rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name)[0].performClick()

    assert(wasProgressChanged)
  }

  @Test
  fun onProgress_incomplete() {
    val chapterCount = recipe.chapters.size

    // Swipe to next page
    rule.onNodeWithTag(Tags.PAGER.name)
      .performTouchInput { swipeLeft() }

    // Check only one checkbox
    rule.onAllNodesWithTag(Tags.PROGRESS_CHECKBOX.name)[0].performClick()

    // Swipe to last page
    repeat(chapterCount){
      rule.onNodeWithTag(Tags.PAGER.name)
        .performTouchInput { swipeLeft() }
    }

    rule.onNodeWithText("Valmis!").assertDoesNotExist()
  }

  @Test
  fun onProgress_completed() {
    chapterUiStates = chapterUiStates.map {
      it.copy(progress = it.progress.map { true })
    }

    // Swipe to last page
    repeat(chapterUiStates.size + 1){
      rule.onNodeWithTag(Tags.PAGER.name)
        .performTouchInput { swipeLeft() }
    }

    rule.onNodeWithText("Valmis!").assertExists()
  }

  @Test
  fun onSetAsFavorite() {
    rule.onNodeWithContentDescription(R.string.description_more_options).performClick()
    rule.onNodeWithText(R.string.button_text_add_to_favorites).performClick()

    assert(favoriteState)
  }

  @Test
  fun onSetAsNonFavorite() {
    favoriteState = true
    rule.onNodeWithContentDescription(R.string.description_more_options).performClick()
    rule.onNodeWithText(R.string.button_text_remove_from_favorites).performClick()

    assertFalse(favoriteState)
  }
}