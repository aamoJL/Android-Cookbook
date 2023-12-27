package com.aamo.cookbook.ui.screen.editRecipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.FormNumberField
import com.aamo.cookbook.ui.components.FormTextField
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
    val servings: Int = 1,
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
    )
  ) {
    fun toRecipe(): Recipe {
      return Recipe(name, category, subCategory, servings, chapters, id)
    }
  }

  data class ChapterScreenUiState(
    val number: Int = 1, // Chapters order number
    val name: String = "",
    val steps: Set<Recipe.Chapter.Step> = emptySet()
  ) {
    fun toChapter(): Recipe.Chapter {
      return Recipe.Chapter(name, steps)
    }
  }

  data class StepScreenUiState(
    val description: String = "",
    val ingredients: Set<Recipe.Ingredient> = emptySet()
  ) {
    fun toStep(): Recipe.Chapter.Step {
      return Recipe.Chapter.Step(description, ingredients)
    }
  }

  data class IngredientScreenUiState(
    val name: String = "",
    val amount: Float = 0f,
    val unit: String = ""
  ) {
    fun toIngredient(): Recipe.Ingredient {
      return Recipe.Ingredient(name, amount, unit)
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
        chapter = _infoUiState.value.chapters.elementAtOrNull(value) ?: Recipe.Chapter(""),
        number = value + 1
      )
    }
  var selectedStepIndex: Int = -1
    set(value) {
      field = value
      initStepUiState(_chapterUiState.value.steps.elementAtOrNull(value) ?: Recipe.Chapter.Step(""))
    }
  var selectedIngredientIndex: Int = -1
    set(value) {
      field = value
      initIngredientUiState(
        _stepUiState.value.ingredients.elementAtOrNull(value)
          ?: Recipe.Ingredient("", 0f, "")
      )
    }

  fun initInfoUiState(recipe: Recipe) {
    _infoUiState.update { s ->
      s.copy(
        id = recipe.id,
        name = recipe.name,
        category = recipe.category,
        subCategory = recipe.subCategory,
        servings = recipe.servings
      )
    }
  }

  private fun initChapterUiState(chapter: Recipe.Chapter, number: Int) {
    _chapterUiState.update { s ->
      s.copy(
        number = number,
        name = chapter.name,
        steps = chapter.steps
      )
    }
  }

  private fun initStepUiState(step: Recipe.Chapter.Step) {
    _stepUiState.update { s ->
      s.copy(
        description = step.description,
        ingredients = step.ingredients
      )
    }
  }

  private fun initIngredientUiState(ingredient: Recipe.Ingredient) {
    _ingredientUiState.update { s ->
      s.copy(
        name = ingredient.name,
        amount = ingredient.amount,
        unit = ingredient.unit
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
        chapters = chapters.toSet()
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
        steps = steps.toSet()
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
        ingredients = ingredients.toSet()
      )
    }
  }

  fun setRecipeName(value: String) = _infoUiState.update { s -> s.copy(name = value) }
  fun setCategory(value: String) = _infoUiState.update { s -> s.copy(category = value) }
  fun setSubCategory(value: String) = _infoUiState.update { s -> s.copy(subCategory = value) }
  fun setServings(value: Int) = _infoUiState.update { s -> s.copy(servings = value) }
  fun setChapterName(value: String) = _chapterUiState.update { s -> s.copy(name = value) }
  fun setStepDescription(value: String) = _stepUiState.update { s -> s.copy(description = value) }
  fun setIngredientName(value: String) = _ingredientUiState.update { s -> s.copy(name = value) }
  fun setIngredientUnit(value: String) = _ingredientUiState.update { s -> s.copy(unit = value) }
  fun setIngredientAmount(value: Float) = _ingredientUiState.update { s -> s.copy(amount = value) }
}

class EditRecipeScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    onEditChapter: (index: Int) -> Unit,
    onSubmitChanges: () -> Unit,
    modifier: Modifier = Modifier
  ) {
    val uiState by viewModel.infoUiState.collectAsState()
    val formIsValid by remember(uiState) {
      mutableStateOf(formValidation(uiState))
    }

    Column(modifier = modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .weight(1f, true)
          .padding(8.dp)
      ) {
        ElevatedCard(
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier
        ) {
          InfoForm(viewModel = viewModel)
        }
        Spacer(modifier = Modifier.padding(8.dp))
        ElevatedCard(
          modifier = Modifier.weight(1f, true)
        ) {
          ListTitleBar(title = "Kappaleet", onAddClick = {
            onEditChapter(uiState.chapters.size)
          })
          ChapterList(viewModel, onEditChapter)
        }
      }
    }
  }

  @Composable
  fun InfoForm(viewModel: EditRecipeViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.infoUiState.collectAsState()

    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = modifier
        .padding(16.dp)
        .fillMaxWidth()
    ) {
      Row {
        Text(text = "Tiedot", style = MaterialTheme.typography.titleLarge)
      }
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
          onValueChange = { viewModel.setServings(it ?: 1) },
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
    val scrollState = rememberScrollState()

    Column(modifier = modifier.verticalScroll(scrollState)) {
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
          Modifier
            .padding(start = 16.dp)
            .width(IntrinsicSize.Min)
        ) {
          for ((index, step) in chapter.steps.withIndex()) {
            Text(
              text = "${index + 1}. ${step.description}${if (step.ingredients.isEmpty()) "." else ":"}",
              style = MaterialTheme.typography.titleSmall
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
              for (ingredient in step.ingredients) {
                Row {
                  Text(
                    text = ingredient.amount.toStringWithoutZero(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
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

  @Composable
  fun SaveButton(enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
      Button(
        enabled = enabled,
        onClick = { onClick() },
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center)
      ) {
        Text(text = "Tallenna")
      }
    }
  }

  @Composable
  fun ListTitleBar(title: String, onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = modifier.fillMaxWidth()) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .padding(vertical = 8.dp, horizontal = 16.dp)
          .fillMaxWidth()
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge
        )
        OutlinedIconButton(
          onClick = { onAddClick() },
        ) {
          Icon(Icons.Filled.Add, "Add new item.")
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
