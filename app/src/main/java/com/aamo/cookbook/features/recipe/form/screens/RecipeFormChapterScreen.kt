package com.aamo.cookbook.features.recipe.form.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields
import kotlinx.serialization.Serializable

@Serializable
data class RecipeFormChapterScreen(val index: Int?)

fun NavGraphBuilder.recipeFormChapterScreen(
  formData: () -> RecipeFormChapterFields,
  stepsData: List<RecipeFormStepFields>,
  onNewStep: () -> Unit,
  onBack: () -> Unit,
) {
  composable<RecipeFormChapterScreen> {
    val uuid by remember { mutableStateOf(formData().uuid.toString()) }
    Text(uuid)
  }
}