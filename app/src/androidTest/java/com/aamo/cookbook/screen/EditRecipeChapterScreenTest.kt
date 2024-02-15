package com.aamo.cookbook.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.ChapterWithStepsAndIngredients
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeChapterScreenContent
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditRecipeChapterScreenTest {
  private var uiState by mutableStateOf(EditRecipeViewModel.ChapterScreenUiState())
  private var wasClicked = false
  private var wasDismissed = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        EditRecipeChapterScreenContent(
          uiState = uiState,
          onFormStateChange = { uiState = uiState.copy(formState = it) },
          onDeleteStep = { true.also { wasDismissed = true } },
          onEditStep = { wasClicked = true },
          onSubmitChanges = { wasClicked = true },
          onBack = { wasClicked = true },
        )
      }
    }
  }

  /**
   * Sets ui state to represent a new chapter
   */
  private fun withNewChapter() {
    uiState = EditRecipeViewModel.ChapterScreenUiState.fromChapter(
      chapter = ChapterWithStepsAndIngredients(Chapter()),
      index = -1
    )
    wasClicked = false
    wasDismissed = false
  }

  /**
   * Sets ui state to represent an existing chapter
   */
  private fun withExistingChapter() {
    val index = 0
    uiState = EditRecipeViewModel.ChapterScreenUiState.fromChapter(
      chapter = Mocker.mockRecipeList().first().chapters[index],
      index = index
    )
    wasClicked = false
    wasDismissed = false
  }

  @Test
  fun pageTitle_equals() {
    withNewChapter().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_new_chapter))
    }

    withExistingChapter().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_existing_chapter))
    }
  }

  @Test
  fun backButton_isVisible() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).assertExists()
  }

  @Test
  fun onBack_noChanges() {
    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    assert(wasClicked)
    rule.onNodeWithText(R.string.dialog_title_unsaved_default).assertDoesNotExist()
  }

  @Test
  fun onBack_dialogVisible() {
    uiState = uiState.copy(unsavedChanges = true)

    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    assertFalse(wasClicked) // OnBack happens only if the user confirms the dialog
    rule.onNodeWithText(R.string.dialog_title_unsaved_default).assertExists()
  }

  @Test
  fun onBack_dialogDismiss() {
    uiState = uiState.copy(unsavedChanges = true)

    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    rule.onNodeWithText(R.string.dialog_dismiss_default).performClick()
    assertFalse(wasClicked) // OnBack happens only if the user confirms the dialog
  }

  @Test
  fun onBack_dialogConfirm() {
    uiState = uiState.copy(unsavedChanges = true)

    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    rule.onNodeWithText(R.string.dialog_confirm_unsaved_default).performClick()
    assert(wasClicked) // OnBack happens only if the user confirms the dialog
  }

  @Test
  fun saveButtonState() {
    withNewChapter().apply {
      rule.onNodeWithText(R.string.button_text_save).performClick()

      assertFalse(wasClicked)
    }

    withExistingChapter().apply {
      rule.onNodeWithText(R.string.button_text_save).performClick()

      assert(wasClicked)
    }
  }

  @Test
  fun onAddNewStep() {
    rule.onNodeWithContentDescription(R.string.description_form_add_new_item).performClick()

    assert(wasClicked)
  }

  @Test
  fun stepItemCount() {
    withNewChapter().apply {
      val nodes = rule.onAllNodesWithTag(Tags.STEP_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.steps.size, nodes.size)
    }

    withExistingChapter().apply {
      val nodes = rule.onAllNodesWithTag(Tags.STEP_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.steps.size, nodes.size)
    }
  }

  @Test
  fun formInputInitValues() {
    withNewChapter().apply {
      rule.onNodeWithText(R.string.textfield_chapter_name).assertTextContains(uiState.formState.name)
    }

    withExistingChapter().apply {
      rule.onNodeWithText(R.string.textfield_chapter_name).assertTextContains(uiState.formState.name)
    }
  }

  @Test
  fun formInputs() {
    val expected = EditRecipeViewModel.ChapterScreenUiState.ChapterFormState(
      name = "name changed",
    )

    rule.onNodeWithText(R.string.textfield_chapter_name).apply {
      performTextClearance()
      performTextInput(expected.name)
    }

    Assert.assertEquals(expected, uiState.formState)
  }

  @Test
  fun onStepDeletion() {
    withExistingChapter().apply {
      rule.onAllNodesWithTag(Tags.STEP_ITEM.name)[0].performTouchInput { swipeRight() }

      rule.mainClock.advanceTimeBy(1000)

      assert(wasDismissed)
    }
  }
}