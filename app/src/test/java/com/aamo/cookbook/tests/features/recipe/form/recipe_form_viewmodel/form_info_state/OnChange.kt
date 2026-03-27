package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_info_state

import com.aamo.cookbook.features.recipe.form.RecipeFormViewModel
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = RecipeFormViewModel.FormInfoState(onChange = { called++ })

    state.name.update("Name").also { Assert.assertEquals(1, called) }
    state.category.update("Cat").also { Assert.assertEquals(2, called) }
    state.subCategory.update("Sub").also { Assert.assertEquals(3, called) }
    state.servings.update(4).also { Assert.assertEquals(4, called) }
    state.note.update("Note").also { Assert.assertEquals(5, called) }
  }
}