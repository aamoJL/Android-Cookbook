package com.aamo.cookbook.tests.features.recipe.form.recipe_form_viewmodel.form_info_state

import com.aamo.cookbook.features.recipe.form.models.states.FormRecipeState
import org.junit.Assert
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class OnChange {
  @Test
  fun onChange() {
    var called = 0
    val state = FormRecipeState(onChange = { called++ })

    state.fields.name.update("Name").also { Assert.assertEquals(1, called) }
    state.fields.category.update("Cat").also { Assert.assertEquals(2, called) }
    state.fields.subCategory.update("Sub").also { Assert.assertEquals(3, called) }
    state.fields.servings.update(4).also { Assert.assertEquals(4, called) }
    state.fields.note.update("Note").also { Assert.assertEquals(5, called) }
  }
}