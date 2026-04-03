package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_ingredient_state

import com.aamo.cookbook.features.recipe.form.models.states.FormIngredientState
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test
import java.util.UUID

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    FormIngredientState(guid = UUID.randomUUID(), onValidityChanged = {}).apply {
      fields.name.update("Desc")
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().validity.value)
    Assert.assertTrue(savableModel().apply { fields.amount.update(3.3) }.validity.value)
    Assert.assertTrue(savableModel().apply { fields.unit.update("Unit") }.validity.value)

    Assert.assertFalse(savableModel().apply { fields.name.update(String.EMPTY) }.validity.value)
  }
}