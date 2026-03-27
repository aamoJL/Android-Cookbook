package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_ingredient_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    RecipeFormViewModel.FormIngredientState(id = UUID.randomUUID(), onChange = {}).apply {
      name.update("Desc")
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave())
    Assert.assertTrue(savableModel().apply { amount.update(3.3) }.canSave())
    Assert.assertTrue(savableModel().apply { unit.update("Unit") }.canSave())

    Assert.assertFalse(savableModel().apply { name.update(String.EMPTY) }.canSave())
  }
}