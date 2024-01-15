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
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.ui.screen.editRecipe.EditRecipeScreenPageContent
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

class EditRecipeScreenTest {
  private var uiState by mutableStateOf(EditRecipeViewModel.InfoScreenUiState())
  private var wasClicked = false

  @get:Rule
  val rule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    rule.setContent {
      CookbookTheme {
        EditRecipeScreenPageContent(
          uiState = uiState,
          onFormStateChange = { uiState = uiState.copy(formState = it) },
          onEditChapter = { wasClicked = true },
          onSubmitChanges = { wasClicked = true },
          onDelete = { wasClicked = true },
          onBack = { wasClicked = true },
        )
      }
    }
  }

  @Test
  fun pageTitle_equals() {
    withNewRecipe().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_new_recipe))
    }

    withExistingRecipe().apply {
      rule.onNodeWithTag(Tags.SCREEN_TITLE.name)
        .assertTextContains(rule.activity.getString(R.string.screen_title_existing_recipe))
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
    withNewRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_save_recipe).performClick()

      assertFalse(wasClicked)
    }

    withExistingRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_save_recipe).performClick()

      assert(wasClicked)
    }
  }

  @Test
  fun deleteButtonState() {
    withNewRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_delete_recipe).assertDoesNotExist()
    }

    withExistingRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_delete_recipe).assertExists()
    }
  }

  @Test
  fun onDelete_dialogVisible() {
    withExistingRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_delete_recipe).performClick()

      rule.onNodeWithText(R.string.dialog_title_delete_recipe).assertExists()

      assertFalse(wasClicked) // onDelete happens only if the user confirms the dialog
    }
  }

  @Test
  fun onDelete_dialogDismiss() {
    withExistingRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_delete_recipe).performClick()

      rule.onNodeWithText(R.string.dialog_dismiss_default).performClick()

      assertFalse(wasClicked) // onDelete happens only if the user confirms the dialog
    }
  }

  @Test
  fun onDelete_dialogConfirm() {
    withExistingRecipe().apply {
      rule.onNodeWithContentDescription(R.string.description_delete_recipe).performClick()

      rule.onNodeWithText(R.string.dialog_confirm_delete_recipe).performClick()

      assert(wasClicked) // onDelete happens only if the user confirms the dialog
    }
  }

  @Test
  fun onAddNewChapter() {
    rule.onNodeWithContentDescription(R.string.description_form_add_new_item).performClick()

    assert(wasClicked)
  }

  @Test
  fun chapterItemCount() {
    withNewRecipe().apply {
      val nodes = rule.onAllNodesWithTag(Tags.CHAPTER_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.chapters.size, nodes.size)
    }

    withExistingRecipe().apply {
      val nodes = rule.onAllNodesWithTag(Tags.CHAPTER_ITEM.name).fetchSemanticsNodes()

      Assert.assertEquals(uiState.chapters.size, nodes.size)
    }
  }

  @Test
  fun onChapterSelection() {
    withExistingRecipe().apply {
      rule.onAllNodesWithTag(Tags.CHAPTER_ITEM.name)[0].performClick()

      assert(wasClicked)
    }
  }

  @Test
  fun formInputInitValues() {
    withExistingRecipe().apply {
      rule.onNodeWithText(R.string.textfield_recipe_name)
        .assertTextContains(uiState.formState.name)

      rule.onNodeWithText(R.string.textfield_recipe_category)
        .assertTextContains(uiState.formState.category)

      rule.onNodeWithText(R.string.textfield_recipe_subcategory)
        .assertTextContains(uiState.formState.subCategory)

      rule.onNodeWithText(R.string.textfield_recipe_servings)
        .assertTextContains(uiState.formState.servings.toString())
    }

    withNewRecipe().apply {
      rule.onNodeWithText(R.string.textfield_recipe_name)
        .assertTextContains(uiState.formState.name)

      rule.onNodeWithText(R.string.textfield_recipe_category)
        .assertTextContains(uiState.formState.category)

      rule.onNodeWithText(R.string.textfield_recipe_subcategory)
        .assertTextContains(uiState.formState.subCategory)

      rule.onNodeWithText(R.string.textfield_recipe_servings)
        .assertTextContains("1")
    }
  }

  @Test
  fun formInputs() {
    val expected = EditRecipeViewModel.InfoScreenUiState.InfoFormState(
      name = "name changed",
      category = "category changed",
      subCategory = "sub changed",
      servings = 3
    )

    rule.onNodeWithText(R.string.textfield_recipe_name).apply {
      performTextClearance()
      performTextInput(expected.name)
    }

    rule.onNodeWithText(R.string.textfield_recipe_category).apply {
      performTextClearance()
      performTextInput(expected.category)
    }

    rule.onNodeWithText(R.string.textfield_recipe_subcategory).apply {
      performTextClearance()
      performTextInput(expected.subCategory)
    }

    rule.onNodeWithText(R.string.textfield_recipe_servings).apply {
      performTextClearance()
      performTextInput(expected.servings.toString())
    }

    Assert.assertEquals(expected, uiState.formState)
  }

  /**
   * Sets ui state to represent an existing recipe
   */
  private fun withExistingRecipe() {
    uiState = EditRecipeViewModel.InfoScreenUiState.fromRecipe(
      recipe = Mocker.mockRecipeList().first()
    )
    wasClicked = false
  }

  /**
   * Sets ui state to represent a new recipe
   */
  private fun withNewRecipe() {
    uiState = EditRecipeViewModel.InfoScreenUiState.fromRecipe(
      recipe = RecipeWithChaptersStepsAndIngredients(Recipe())
    )
    wasClicked = false
  }
}