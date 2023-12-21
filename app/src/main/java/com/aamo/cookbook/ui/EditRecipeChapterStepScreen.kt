package com.aamo.cookbook.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.ui.components.FormFloatField
import com.aamo.cookbook.ui.components.FormTextField

class EditRecipeChapterStepScreen {
  @Composable
  fun Screen(
    viewModel: EditRecipeViewModel,
    chapterIndex: Int,
    stepIndex: Int,
    modifier: Modifier = Modifier
  ) {

//    Column(modifier = modifier
//      .fillMaxSize()
//      .padding(4.dp)) {
//      FormTextField(
//        value = descriptionValue,
//        onValueChange = { descriptionValue = it },
//        label = "vaiheen kuvaus"
//      )
//      Button(onClick = {
//        stepIngredients.add(Recipe.Ingredient("", 0f, ""))
//      }) {
//        Text(text = "Lisää uusi ainesosa")
//      }
//      LazyColumn {
//        itemsIndexed(stepIngredients) { index, item ->
//          IngredientItem(ingredient = item)
//        }
//      }
//    }
  }

  @Composable
  fun IngredientItem(
    ingredient: Recipe.Ingredient,
  ) {
    var nameValue by rememberSaveable(ingredient.id) { mutableStateOf(ingredient.name) }
    var unitValue by rememberSaveable(ingredient.id) { mutableStateOf(ingredient.unit) }
    var amountValue by rememberSaveable(ingredient.id) { mutableStateOf(ingredient.amount.toString()) }

    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
      FormTextField(
        value = nameValue,
        onValueChange = { nameValue = it },
        label = "Ainesosa",
        modifier = Modifier.weight(1f, true)
      )
      FormTextField(
        value = unitValue,
        onValueChange = { unitValue = it },
        label = "Mitta",
        modifier = Modifier.weight(.5f)
      )
      FormFloatField(
        value = amountValue.toFloatOrNull(),
        onValueChange = { amountValue = it?.toString() ?: "" },
        label = "Määrä",
        modifier = Modifier.weight(.5f)
      )
    }
  }
}