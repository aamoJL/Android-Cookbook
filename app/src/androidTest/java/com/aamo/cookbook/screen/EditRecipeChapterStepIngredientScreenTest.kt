package com.aamo.cookbook.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.aamo.cookbook.Mocker
import com.aamo.cookbook.R
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeChapterStepIngredientScreenContent
import com.aamo.cookbook.ui.theme.CookbookTheme
import com.aamo.cookbook.utility.Tags
import com.aamo.cookbook.utility.onNodeWithContentDescription
import com.aamo.cookbook.utility.onNodeWithText
import com.aamo.cookbook.utility.toStringWithoutZero
import com.aamo.cookbook.viewModel.EditRecipeViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditRecipeChapterStepIngredientScreenTest {
  private var uiState by mutableStateOf(EditRecipeViewModel.IngredientScreenUiState())
  private var wasClicked = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        EditRecipeChapterStepIngredientScreenContent(
          uiState = uiState,
          onFormStateChange = { uiState = uiState.copy(formState = it) },
          onSubmitChanges = { wasClicked = true },
          onBack = { wasClicked = true },
        )
      }
    }
  }

  /**
   * Sets ui state to represent a new ingredient
   */
  private fun withNewIngredient() {
    uiState = EditRecipeViewModel.IngredientScreenUiState.fromIngredient(
      ingredient = Ingredient(),
      index = 0
    )
    wasClicked = false
  }

  /**
   * Sets ui state to represent an existing ingredient
   */
  private fun withExistingIngredient() {
    uiState = EditRecipeViewModel.IngredientScreenUiState.fromIngredient(
      ingredient = Mocker.mockRecipeList().first().chapters.first().steps.first().ingredients.first(),
      index = 0
    )
    wasClicked = false
  }

  @Test
  fun pageTitle_equals() {
    withNewIngredient().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_new_ingredient))
    }

    withExistingIngredient().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_existing_ingredient))
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
    withNewIngredient().apply {
      rule.onNodeWithText(R.string.button_text_save).performClick()

      Assert.assertFalse(wasClicked)
    }

    withExistingIngredient().apply {
      rule.onNodeWithText(R.string.button_text_save).performClick()

      assert(wasClicked)
    }
  }

  @Test
  fun formInputInitValues() {
    withNewIngredient().apply {
      rule.onNodeWithText(R.string.textfield_ingredient_name)
        .assertTextContains(uiState.formState.name)
      rule.onNodeWithText(R.string.textfield_ingredient_amount)
        .assertTextContains("")
      rule.onNodeWithText(R.string.textfield_ingredient_unit)
        .assertTextContains(uiState.formState.unit)
    }

    withExistingIngredient().apply {
      rule.onNodeWithText(R.string.textfield_ingredient_name)
        .assertTextContains(uiState.formState.name)
      rule.onNodeWithText(R.string.textfield_ingredient_amount)
        .assertTextContains(uiState.formState.amount!!.toStringWithoutZero())
      rule.onNodeWithText(R.string.textfield_ingredient_unit)
        .assertTextContains(uiState.formState.unit)
    }
  }

  @Test
  fun formInputs() {
    val expected = EditRecipeViewModel.IngredientScreenUiState.IngredientFormState(
      name = "name changed",
      amount = 3.5f,
      unit = "unit changed"
    )

    rule.onNodeWithText(R.string.textfield_ingredient_name).apply {
      performTextClearance()
      performTextInput(expected.name)
    }

    rule.onNodeWithText(R.string.textfield_ingredient_amount).apply {
      performTextClearance()
      performTextInput(expected.amount.toString())
    }

    rule.onNodeWithText(R.string.textfield_ingredient_unit).apply {
      performTextClearance()
      performTextInput(expected.unit)
    }

    Assert.assertEquals(expected, uiState.formState)
  }
}