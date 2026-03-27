package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_info_state

import com.aamo.cookbook.features.recipe.form.models.states.FormRecipeState
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class CanSave {
  val savableModel = {
    FormRecipeState().apply {
      fields.apply {
        name.update("Name")
        category.update("Cat")
        servings.update(3)
      }
      addChapter().apply {
        fields.apply {
          name.update("Name")
        }
        addStep().fields.apply {
          description.update("Desc")
        }
      }
    }
  }

  @Test
  fun canSave() {
    Assert.assertTrue(savableModel().canSave.value)
    Assert.assertTrue(savableModel().apply { fields.subCategory.update("Sub") }.canSave.value)
    Assert.assertTrue(savableModel().apply { fields.note.update("Note") }.canSave.value)

    Assert.assertFalse(savableModel().apply { fields.name.update(String.EMPTY) }.canSave.value)
    Assert.assertFalse(savableModel().apply { fields.category.update(String.EMPTY) }.canSave.value)
    Assert.assertFalse(savableModel().apply { fields.servings.update(null) }.canSave.value)
    Assert.assertFalse(savableModel().apply { fields.servings.update(0) }.canSave.value)
    Assert.assertFalse(savableModel().apply { fields.servings.update(-1) }.canSave.value)
  }
}