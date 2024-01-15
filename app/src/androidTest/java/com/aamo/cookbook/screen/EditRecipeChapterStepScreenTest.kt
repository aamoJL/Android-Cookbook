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
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Step
import com.aamo.cookbook.model.StepWithIngredients
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeChapterStepScreenContent
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditRecipeChapterStepScreenTest {
  private var uiState by mutableStateOf(EditRecipeViewModel.StepScreenUiState())
  private var wasClicked = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        EditRecipeChapterStepScreenContent(
          uiState = uiState,
          onFormStateChange = { uiState = uiState.copy(formState = it) },
          onEditIngredient = { wasClicked = true },
          onSubmitChanges = { wasClicked = true },
          onBack = { wasClicked = true },
        )
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    withNewStep().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_new_step))
    }

    withExistingStep().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_existing_step))
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

    Assert.assertFalse(wasClicked) // OnBack happens only if the user confirms the dialog
    rule.onNodeWithText(R.string.dialog_title_unsaved_default).assertExists()
  }

  @Test
  fun onBack_dialogDismiss() {
    uiState = uiState.copy(unsavedChanges = true)

    rule.onNodeWithContentDescription(R.string.description_screen_back).performClick()

    rule.onNodeWithText(R.string.dialog_dismiss_default).performClick()
    Assert.assertFalse(wasClicked) // OnBack happens only if the user confirms the dialog
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
    withNewStep().apply {
      rule.onNodeWithText(R.string.button_text_save).performClick()

      Assert.assertFalse(wasClicked)
    }

    withExistingStep().apply {
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
  fun ingredientsItemCount() {
    withNewStep().apply {
      val nodes = rule.onAllNodesWithTag(Tags.INGREDIENT_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.ingredients.size, nodes.size)
    }

    withExistingStep().apply {
      val nodes = rule.onAllNodesWithTag(Tags.INGREDIENT_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.ingredients.size, nodes.size)
    }
  }

  @Test
  fun formInputInitValues() {
    withNewStep().apply {
      rule.onNodeWithText(R.string.textfield_step_description).assertTextContains(uiState.formState.description)
    }

    withExistingStep().apply {
      rule.onNodeWithText(R.string.textfield_step_description).assertTextContains(uiState.formState.description)
    }
  }

  @Test
  fun formInputs() {
    val expected = EditRecipeViewModel.StepScreenUiState.StepFormState(
      description = "description changed",
    )

    rule.onNodeWithText(R.string.textfield_step_description).apply {
      performTextClearance()
      performTextInput(expected.description)
    }

    Assert.assertEquals(expected, uiState.formState)
  }

  /**
   * Sets ui state to represent a new step
   */
  private fun withNewStep() {
    uiState = EditRecipeViewModel.StepScreenUiState.fromStep(
      step = StepWithIngredients(Step())
    )
    wasClicked = false
  }

  /**
   * Sets ui state to represent an existing step
   */
  private fun withExistingStep() {
    uiState = EditRecipeViewModel.StepScreenUiState.fromStep(
      step = Mocker.mockRecipeList().first().chapters.first().steps.first()
    )
    wasClicked = false
  }
}