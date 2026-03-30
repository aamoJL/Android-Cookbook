package com.aamo.cookbook.features.recipe.form.models.states

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.utility.viewmodels.ViewModelState
import com.aamo.cookbook.utility.viewmodels.ViewModelStateList

class FormRecipeState(
  private val model: RecipeWithChaptersStepsAndIngredients = RecipeWithChaptersStepsAndIngredients(
    recipe = Recipe()
  ),
  val onChange: () -> Unit = {},
  onValidityChanged: () -> Unit = {},
) {
  class Fields(private val model: Recipe = Recipe(), private val onChange: () -> Unit) {
    val name = ViewModelState(model.name).onChange { onChange() }
    val category = ViewModelState(model.category).onChange { onChange() }
    val subCategory = ViewModelState(model.subCategory).onChange { onChange() }
    val servings =
      ViewModelState<Int?>(model.servings).transformation { value -> if (value != null && value < 1) null else value }
        .onChange { onChange() }
    val note = ViewModelState(model.note).onChange {
      if (it.isNotEmpty()) noteFieldToggleValue = true
      onChange()
    }

    var noteFieldToggleValue by mutableStateOf(model.note.isNotEmpty())
    var isDirty by mutableStateOf(false)
      private set
    var requiredFieldsFilled by mutableStateOf(requiredFieldsFilled())
      private set

    fun getModel(): Recipe {
      return model.copy(
        name = name.value,
        category = category.value,
        subCategory = subCategory.value,
        servings = servings.value ?: 0,
        note = note.value,
      )
    }

    private fun onChange() {
      isDirty = true
      requiredFieldsFilled = requiredFieldsFilled()
      onChange.invoke()
    }

    private fun requiredFieldsFilled(): Boolean {
      if (name.value.isBlank()) return false
      if (category.value.isBlank()) return false
      servings.value.also { if (it == null || it < 1) return false }
      return true
    }
  }

  val fields = Fields(model = model.recipe, onChange = { onChange() })
  val chapterStates =
    ViewModelStateList(model.chapters.map { createChapterState(model = it) }).onChange { onChange() }
  val validity = ViewModelState(checkValidity()).onChange { onValidityChanged() }

  fun addChapter(): FormChapterState {
    return createChapterState(model = ChapterWithStepsAndIngredients(chapter = Chapter())).also {
      chapterStates.add(it)
    }
  }

  fun getModel(): RecipeWithChaptersStepsAndIngredients {
    return model.copy(
      recipe = fields.getModel(),
      chapters = chapterStates.values.map { it.getModel() },
    )
  }

  private fun createChapterState(model: ChapterWithStepsAndIngredients): FormChapterState {
    return FormChapterState(model = model, onValidityChanged = { onChange() }, onChange = onChange)
  }

  private fun onChange() {
    validity.update(checkValidity())
    onChange.invoke()
  }

  private fun checkValidity(): Boolean {
    if (chapterStates.values.isEmpty()) return false
    if (chapterStates.values.any { !it.validity.value }) return false
    return fields.requiredFieldsFilled
  }
}