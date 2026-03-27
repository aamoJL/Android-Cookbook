package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_info_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import org.junit.Assert
import org.junit.Test

class Transformation {
  @Test
  fun servings() {
    Assert.assertNull(RecipeFormViewModel.FormInfoState(onChange = { }).apply {
      servings.update(0)
    }.servings.value)

    Assert.assertNull(RecipeFormViewModel.FormInfoState(onChange = { }).apply {
      servings.update(null)
    }.servings.value)

    Assert.assertNull(RecipeFormViewModel.FormInfoState(onChange = { }).apply {
      servings.update(-1)
    }.servings.value)

    Assert.assertEquals(
      123,
      RecipeFormViewModel.FormInfoState(onChange = { })
        .apply { servings.update(123) }.servings.value
    )
  }
}