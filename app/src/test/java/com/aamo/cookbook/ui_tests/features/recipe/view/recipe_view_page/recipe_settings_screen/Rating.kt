package com.aamo.cookbook.ui_tests.features.recipe.view.recipe_view_page.recipe_settings_screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.aamo.cookbook.R
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.test_utility.ui.rules.PageTest
import com.aamo.cookbook.test_utility.ui.rules.waitForDisplayed
import com.aamo.cookbook.test_utility.ui.rules.waitForLoading
import com.aamo.cookbook.ui.components.inputs.FiveStarRatingTags
import com.aamo.cookbook.utility.extensions.general.EMPTY
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("HardCodedStringLiteral")
@Config(qualifiers = "w1000dp-h1000dp-480dpi")
@RunWith(RobolectricTestRunner::class)
class Rating : PageTest() {
  lateinit var recipe: RecipeWithChaptersStepsAndIngredients

  @Before
  fun setup() = runTest {
    recipe = toRecipeViewPage(RecipeMocker.getFullMocker().apply {
      modify { it.copy(name = "Recipe") }
      chapters.first().modify { it.copy(name = "Chapter 1") }
    }.mock())
    waitForLoading()
    rule.onRoot().performTouchInput { swipeRight() }
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun `rate unrated`() = runTest {
    val starNodes =
      rule.onAllNodesWithTag(FiveStarRatingTags.RATING_STAR.name, useUnmergedTree = true)

    assertEquals(5, starNodes.fetchSemanticsNodes().size)

    starNodes.assertAll(
      hasContentDescription(
        getString(R.string.cd_star_rating_star_icon_unselected, String.EMPTY), substring = true
      )
    )

    rule.onNodeWithContentDescription(
      getString(R.string.cd_star_rating_star_icon_unselected, "3")
    ).performClick()

    rule.onNodeWithContentDescription(getString(R.string.cd_star_rating_star_icon_selected, "1"))
      .waitForDisplayed()

    starNodes[0].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "1")
    )
    starNodes[1].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "2")
    )
    starNodes[2].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "3")
    )
    starNodes[3].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_unselected, "4")
    )
    starNodes[4].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_unselected, "5")
    )
  }

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun `unrate rated`() = runTest {
    getDao().upsert(RecipeRating(recipeId = recipe.recipe.id, ratingOutOfFive = 3))

    val starNodes =
      rule.onAllNodesWithTag(FiveStarRatingTags.RATING_STAR.name, useUnmergedTree = true)

    assertEquals(5, starNodes.fetchSemanticsNodes().size)

    rule.onNodeWithContentDescription(getString(R.string.cd_star_rating_star_icon_selected, "3"))
      .waitForDisplayed()

    starNodes[0].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "1")
    )
    starNodes[1].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "2")
    )
    starNodes[2].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_selected, "3")
    )
    starNodes[3].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_unselected, "4")
    )
    starNodes[4].assertContentDescriptionContains(
      getString(R.string.cd_star_rating_star_icon_unselected, "5")
    )

    rule.onNodeWithContentDescription(
      getString(R.string.cd_star_rating_star_icon_selected, "3")
    ).performClick()

    rule.onNodeWithContentDescription(getString(R.string.cd_star_rating_star_icon_unselected, "3"))
      .waitForDisplayed()

    starNodes.assertAll(
      hasContentDescription(
        getString(R.string.cd_star_rating_star_icon_unselected, String.EMPTY), substring = true
      )
    )
  }
}