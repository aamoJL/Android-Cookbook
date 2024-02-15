package com.aamo.cookbook.screen

import android.net.Uri
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toFile
import androidx.test.platform.app.InstrumentationRegistry
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.R
import com.aamo.cookbook.service.IOService
import com.aamo.cookbook.ui.screen.recipeScreen.RecipeScreenContent
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.WithActivityResultRegistry
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.viewModel.RecipeScreenViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeScreenTest {
  private val recipe = Mocker.mockRecipeList().first()
  private var chapterUiStates by mutableStateOf<List<RecipeScreenViewModel.ChapterPageUiState>>(emptyList())
  private var completedPageUiState by mutableStateOf(RecipeScreenViewModel.CompletedPageUiState())
  private var servingsState by mutableStateOf(RecipeScreenViewModel.ServingsState(recipe.value.servings, recipe.value.servings))
  private var favoriteState by mutableStateOf(false)
  private var wasProgressChanged: Boolean = false
  private var wasClicked: Boolean = false
  private var photoTaken: Uri? = null
  private var ratingState by mutableStateOf(0)

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    // Create the test ActivityResultRegistry
    val testRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?
      ) {
        dispatchResult(requestCode, true)
      }
    }

    rule.setContent {
      chapterUiStates = recipe.chapters.map {
        RecipeScreenViewModel.ChapterPageUiState.fromChapter(it)
      }

      CookbookTheme {
        WithActivityResultRegistry(activityResultRegistry = testRegistry) {
          RecipeScreenContent(
            summaryPageUiState = RecipeScreenViewModel.SummaryPageUiState(
              recipeName = recipe.value.name,
              chaptersWithIngredients = recipe.chapters.map { chapter ->
                Pair(chapter.value.name, chapter.steps.flatMap { it.ingredients })
              }
            ),
            chapterPageUiStates = chapterUiStates,
            completedPageUiState = completedPageUiState,
            servingsState = servingsState,
            favoriteState = favoriteState,
            onProgressChange = { _, _, _ ->
              wasProgressChanged = true
            },
            onBack = { wasClicked = true },
            onEditRecipe = { wasClicked = true },
            onFavoriteChange = { favoriteState = it },
            onServingsCountChange = { servingsState = servingsState.copy(current = it) },
            onThumbnailChange = {
              photoTaken = it
            },
            onRatingChange = { ratingState = it },
            onCopyRecipe = { wasClicked = true },
          )
        }
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

    rule.onNodeWithText(R.string.text_rate_the_recipe).assertDoesNotExist()
  }

  @Test
  fun onProgress_completed() {
    toCompletedPage()
    rule.onNodeWithText(R.string.text_rate_the_recipe).assertExists()
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

  @Test
  fun onTakeThumbnailPhoto() {
    toCompletedPage()
    rule.onNodeWithContentDescription(R.string.description_take_a_photo).performClick()

    val ioService = IOService(InstrumentationRegistry.getInstrumentation().targetContext)
    val fileName = ioService.getFileNameWithSuffixFromUri(photoTaken ?: Uri.EMPTY) ?: ""

    assertNotNull(photoTaken)
    assertTrue(fileName.isNotEmpty())
    assertTrue(ioService.getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName).toFile().exists())
  }

  @Test
  fun onDeleteThumbnailPhoto() {
    toCompletedPage()
    rule.onNodeWithContentDescription(R.string.description_take_a_photo).performClick()

    val ioService = IOService(InstrumentationRegistry.getInstrumentation().targetContext)
    val fileName = ioService.getFileNameWithSuffixFromUri(photoTaken ?: Uri.EMPTY) ?: ""

    completedPageUiState = completedPageUiState.copy(recipeThumbnail = fileName)

    rule.onNodeWithContentDescription(R.string.description_delete_photo).performClick()

    assertEquals(Uri.EMPTY, photoTaken)
  }

  @Test
  fun onStarRating() {
    val rating = 3

    toCompletedPage()
    rule.onNodeWithContentDescription(
      rule.activity.getString(
        R.string.description_star_rating_star_icon,
        rating.toString()
      )
    ).performClick()

    assertEquals(rating, ratingState)
  }

  /**
   * Completes the recipe progress and swipes the pages to the completed page
   */
  private fun toCompletedPage(){
    chapterUiStates = chapterUiStates.map {
      it.copy(progress = it.progress.map { true })
    }

    // Swipe to last page
    repeat(chapterUiStates.size + 1){
      rule.onNodeWithTag(Tags.PAGER.name)
        .performTouchInput { swipeLeft() }
    }
  }
}