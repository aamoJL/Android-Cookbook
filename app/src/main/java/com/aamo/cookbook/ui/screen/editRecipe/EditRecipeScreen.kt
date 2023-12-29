package com.aamo.cookbook.ui.screen.editRecipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.BasicTopAppBar
import com.aamo.cookbook.ui.components.form.FormBase
import com.aamo.cookbook.ui.components.form.FormList
import com.aamo.cookbook.ui.components.form.FormNumberField
import com.aamo.cookbook.ui.components.form.FormTextField
import com.aamo.cookbook.ui.components.form.UnsavedDialog
import com.aamo.cookbook.utility.toStringWithoutZero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class EditRecipeViewModel() : ViewModel() {
  data class InfoScreenUiState(
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val category: String = "",
    val subCategory: String = "",
    val servings: Int? = null,
    val chapters: Set<Recipe.Chapter> = setOf(
      Recipe.Chapter(
        "Taikina", setOf(
          Recipe.Chapter.Step(
            "Sekoita", setOf(
              Recipe.Ingredient("Kananmuna", 2f, "kpl"),
              Recipe.Ingredient("Kahvi", 50f, "dl")
            )
          ),
          Recipe.Chapter.Step(
            "Lisää joukkoon", setOf(
              Recipe.Ingredient("Jauho", 500f, "g"),
              Recipe.Ingredient("Maito", 50f, "ml")
            )
          )
        )
      )
    ),
    val unsavedChanges: Boolean = false
  ) {
    val screenTitle = when (id) {
      UUID(0, 0) -> "Lisää uusi resepti"
      else -> "Muokkaa reseptiä"
    }

    fun toRecipe(): Recipe {
      return Recipe(name, category, subCategory, servings ?: 1, chapters, id)
    }
  }

  data class ChapterScreenUiState(
    val id: UUID = UUID(0, 0),
    val number: Int = 1, // Chapters order number
    val name: String = "",
    val steps: Set<Recipe.Chapter.Step> = emptySet(),
    val unsavedChanges: Boolean = false
  ) {
    val screenTitle = when (id) {
      UUID(0, 0) -> "Lisää uusi kappale"
      else -> "Muokkaa kappaletta"
    }

    fun toChapter(): Recipe.Chapter {
      return Recipe.Chapter(name, steps)
    }
  }

  data class StepScreenUiState(
    val id: UUID = UUID(0, 0),
    val description: String = "",
    val ingredients: Set<Recipe.Ingredient> = emptySet(),
    val unsavedChanges: Boolean = false
  ) {
    val screenTitle = when (id) {
      UUID(0, 0) -> "Lisää uusi vaihe"
      else -> "Muokkaa vaihetta"
    }

    fun toStep(): Recipe.Chapter.Step {
      return Recipe.Chapter.Step(description, ingredients)
    }
  }

  data class IngredientScreenUiState(
    val id: UUID = UUID(0, 0),
    val name: String = "",
    val amount: Float? = null,
    val unit: String = "",
    val unsavedChanges: Boolean = false
  ) {
    val screenTitle = when (id) {
      UUID(0, 0) -> "Lisää uusi ainesosa"
      else -> "Muokkaa ainesosaa"
    }

    fun toIngredient(): Recipe.Ingredient {
      return Recipe.Ingredient(name, amount ?: 0f, unit)
    }
  }

  private val _infoUiState = MutableStateFlow(InfoScreenUiState())
  val infoUiState: StateFlow<InfoScreenUiState> = _infoUiState.asStateFlow()

  private val _chapterUiState = MutableStateFlow(ChapterScreenUiState())
  val chapterUiState: StateFlow<ChapterScreenUiState> = _chapterUiState.asStateFlow()

  private val _stepUiState = MutableStateFlow(StepScreenUiState())
  val stepUiState: StateFlow<StepScreenUiState> = _stepUiState.asStateFlow()

  private val _ingredientUiState = MutableStateFlow(IngredientScreenUiState())
  val ingredientUiState: StateFlow<IngredientScreenUiState> = _ingredientUiState.asStateFlow()

  var selectedChapterIndex: Int = -1
    set(value) {
      field = value
      initChapterUiState(
        chapter = _infoUiState.value.chapters.elementAtOrNull(value) ?: Recipe.Chapter(
          "",
          id = UUID(0, 0)
        ),
        number = value + 1
      )
    }
  var selectedStepIndex: Int = -1
    set(value) {
      field = value
      initStepUiState(
        _chapterUiState.value.steps.elementAtOrNull(value) ?: Recipe.Chapter.Step(
          "",
          id = UUID(0, 0)
        )
      )
    }
  var selectedIngredientIndex: Int = -1
    set(value) {
      field = value
      initIngredientUiState(
        _stepUiState.value.ingredients.elementAtOrNull(value)
          ?: Recipe.Ingredient("", 0f, "", UUID(0, 0))
      )
    }

  fun initInfoUiState(recipe: Recipe) {
    _infoUiState.update { s ->
      s.copy(
        id = recipe.id,
        name = recipe.name,
        category = recipe.category,
        subCategory = recipe.subCategory,
        servings = recipe.servings,
        unsavedChanges = false
      )
    }
  }

  private fun initChapterUiState(chapter: Recipe.Chapter, number: Int) {
    _chapterUiState.update { s ->
      s.copy(
        id = chapter.id,
        number = number,
        name = chapter.name,
        steps = chapter.steps,
        unsavedChanges = false
      )
    }
  }

  private fun initStepUiState(step: Recipe.Chapter.Step) {
    _stepUiState.update { s ->
      s.copy(
        id = step.id,
        description = step.description,
        ingredients = step.ingredients,
        unsavedChanges = false
      )
    }
  }

  private fun initIngredientUiState(ingredient: Recipe.Ingredient) {
    _ingredientUiState.update { s ->
      s.copy(
        id = ingredient.id,
        name = ingredient.name,
        amount = ingredient.amount,
        unit = ingredient.unit,
        unsavedChanges = false
      )
    }
  }

  fun applyChapterChanges() {
    val chapters = _infoUiState.value.chapters.toMutableList()
    val chapter = _chapterUiState.value.toChapter()

    if (chapters.elementAtOrNull(selectedChapterIndex) != null) {
      chapters[selectedChapterIndex] = chapter
    } else {
      chapters.add(chapter)
    }

    _infoUiState.update { s ->
      s.copy(
        chapters = chapters.toSet(),
        unsavedChanges = true
      )
    }
  }

  fun applyStepChanges() {
    val steps = _chapterUiState.value.steps.toMutableList()
    val step = _stepUiState.value.toStep()

    if (steps.elementAtOrNull(selectedStepIndex) != null) {
      steps[selectedStepIndex] = step
    } else {
      steps.add(step)
    }

    _chapterUiState.update { s ->
      s.copy(
        steps = steps.toSet(),
        unsavedChanges = true
      )
    }
  }

  fun applyIngredientChanges() {
    val ingredients = _stepUiState.value.ingredients.toMutableList()
    val ingredient = _ingredientUiState.value.toIngredient()

    if (ingredients.elementAtOrNull(selectedIngredientIndex) != null) {
      ingredients[selectedIngredientIndex] = ingredient
    } else {
      ingredients.add(ingredient)
    }

    _stepUiState.update { s ->
      s.copy(
        ingredients = ingredients.toSet(),
        unsavedChanges = true
      )
    }
  }

  fun setRecipeName(value: String) =
    _infoUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setCategory(value: String) =
    _infoUiState.update { s -> s.copy(category = value, unsavedChanges = true) }

  fun setSubCategory(value: String) =
    _infoUiState.update { s -> s.copy(subCategory = value, unsavedChanges = true) }

  fun setServings(value: Int?) =
    _infoUiState.update { s -> s.copy(servings = value, unsavedChanges = true) }

  fun setChapterName(value: String) =
    _chapterUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setStepDescription(value: String) =
    _stepUiState.update { s -> s.copy(description = value, unsavedChanges = true) }

  fun setIngredientName(value: String) =
    _ingredientUiState.update { s -> s.copy(name = value, unsavedChanges = true) }

  fun setIngredientUnit(value: String) =
    _ingredientUiState.update { s -> s.copy(unit = value, unsavedChanges = true) }

  fun setIngredientAmount(value: Float?) =
    _ingredientUiState.update { s -> s.copy(amount = value, unsavedChanges = true) }
}

class EditRecipeScreen {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditChapter: (index: Int) -> Unit,
    onSubmitChanges: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.infoUiState.collectAsState()
    val formIsValid by remember(uiState) {
      mutableStateOf(formValidation(uiState))
    }
    var openUnsavedDialog by remember { mutableStateOf(false) }

    if (openUnsavedDialog) {
      UnsavedDialog(
        onDismiss = {
          openUnsavedDialog = false
        },
        onConfirm = {
          openUnsavedDialog = false
          onBack()
        })
    }

    BackHandler(true) {
      if (uiState.unsavedChanges) {
        openUnsavedDialog = true
      } else {
        onBack()
      }
    }

    Scaffold(
      topBar = {
        BasicTopAppBar(title = uiState.screenTitle, onBack = {
          if (uiState.unsavedChanges) {
            openUnsavedDialog = true
          } else {
            onBack()
          }
        }, actions = {
          IconButton(onClick = onSubmitChanges) {
            Icon(imageVector = Icons.Filled.Done, contentDescription = "Tallenna resepti")
          }
        })
      }
    ) {
      Column(
        modifier = modifier
          .padding(it)
          .padding(8.dp)
      ) {
        InfoForm(viewModel = viewModel)
        Spacer(modifier = Modifier.padding(8.dp))
        ChapterList(viewModel = viewModel, onEditChapter = onEditChapter)
      }
    }
  }

  @Composable
  fun InfoForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.infoUiState.collectAsState()

    FormBase(title = "Reseptin tiedot", modifier = modifier) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        FormTextField(
          value = uiState.name,
          onValueChange = { viewModel.setRecipeName(it) },
          label = "Nimi",
          modifier = Modifier.weight(2f, true)
        )
        FormNumberField(
          value = uiState.servings,
          onValueChange = { viewModel.setServings(it) },
          label = "Annokset",
          modifier = Modifier.weight(1f, true)
        )
      }
      Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        FormTextField(
          value = uiState.category,
          onValueChange = { viewModel.setCategory(it) },
          label = "Kategoria",
          modifier = Modifier.weight(1f)
        )
        FormTextField(
          value = uiState.subCategory,
          onValueChange = { viewModel.setSubCategory(it) },
          label = "(Alakategoria)",
          imeAction = ImeAction.Done,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }

  @Composable
  fun ChapterList(
    viewModel: EditRecipeViewModel,
    onEditChapter: (index: Int) -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.infoUiState.collectAsState()

    FormList(
      title = "Kappaleet",
      onAddClick = {
        onEditChapter(uiState.chapters.size)
      },
      modifier = modifier
    ) {
      for ((index, chapter) in uiState.chapters.withIndex()) {
        ChapterItem(
          number = index + 1,
          chapter = chapter,
          onClick = { onEditChapter(index) },
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .fillMaxWidth()
        )
        Divider()
      }
    }
  }

  @Composable
  fun ChapterItem(
    number: Int,
    chapter: Recipe.Chapter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    Box(modifier = Modifier.clickable {
      onClick()
    }) {
      Column(modifier = modifier.fillMaxWidth()) {
        Text(text = "${number}. ${chapter.name}", style = MaterialTheme.typography.titleMedium)
        Column(
          modifier = Modifier
            .padding(start = 16.dp)
            .width(IntrinsicSize.Min)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for ((index, step) in chapter.steps.withIndex()) {
              Column {
                Text(
                  text = "${index + 1}. ${step.getDescriptionWithFormattedEndChar()}",
                  style = MaterialTheme.typography.bodyMedium
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                  for (ingredient in step.ingredients) {
                    Row {
                      Text(
                        text = ingredient.amount.toStringWithoutZero(),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                          .defaultMinSize(minWidth = 40.dp)
                          .weight(1f)
                      )
                      Text(
                        text = ingredient.unit,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                          .weight(1f)
                          .padding(start = 4.dp)
                      )
                      Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(2f)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private fun formValidation(uiState: EditRecipeViewModel.InfoScreenUiState): Boolean {
    return uiState.name.isNotEmpty()
            && uiState.category.isNotEmpty()
            && uiState.chapters.isNotEmpty()
  }
}
