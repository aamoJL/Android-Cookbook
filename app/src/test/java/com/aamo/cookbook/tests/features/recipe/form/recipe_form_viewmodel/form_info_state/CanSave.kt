package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_info_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    RecipeFormViewModel.FormInfoState(onChange = {}).apply {
      name.update("Name")
      category.update("Cat")
      servings.update(3)
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave())
    Assert.assertTrue(savableModel().apply { subCategory.update("Sub") }.canSave())
    Assert.assertTrue(savableModel().apply { note.update("Note") }.canSave())

    Assert.assertFalse(savableModel().apply { name.update(String.EMPTY) }.canSave())
    Assert.assertFalse(savableModel().apply { category.update(String.EMPTY) }.canSave())
    Assert.assertFalse(savableModel().apply { servings.update(null) }.canSave())
    Assert.assertFalse(savableModel().apply { servings.update(0) }.canSave())
    Assert.assertFalse(savableModel().apply { servings.update(-1) }.canSave())
  }
}