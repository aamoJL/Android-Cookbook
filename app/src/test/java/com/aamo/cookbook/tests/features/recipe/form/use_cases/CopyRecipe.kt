package com.aamo.cookbook.tests.features.recipe.form.use_cases

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aamo.cookbook.R
import com.aamo.cookbook.features.recipe.form.use_cases.copyRecipe
import com.aamo.cookbook.test_utility.RecipeMocker
import com.aamo.cookbook.utility.extensions.general.EMPTY
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CopyRecipe {
  @Test
  fun `returns correct model`() {
    val context: Context = ApplicationProvider.getApplicationContext()
    val recipe = RecipeMocker.getFullMocker().withIds().mock()
    check(recipe.recipe.id != 0L)
    check(recipe.chapters.all { it.chapter.id != 0L })

    val expected = RecipeMocker.getFullMocker().modify {
      it.copy(
        name = context.getString(R.string.text_recipe_name_as_copy, recipe.recipe.name),
        thumbnailUri = String.EMPTY,
      )
    }.mock()
    val actual = copyRecipe(recipe = recipe, context = context)

    Assert.assertEquals(expected, actual)
  }
}