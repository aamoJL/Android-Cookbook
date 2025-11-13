package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields

suspend fun fetchRecipe(
  fetchData: suspend () -> RecipeWithChaptersStepsAndIngredients
): RecipeWithChaptersStepsAndIngredients {
  return fetchData()
}

fun RecipeFormInfoFields.Companion.fromDao(dao: RecipeWithChaptersStepsAndIngredients): RecipeFormInfoFields {
  return dao.let { (r, cs) ->
    RecipeFormInfoFields(
      name = r.name,
      category = r.category,
      subCategory = r.subCategory,
      servings = r.servings,
      note = r.note,
      chapters = cs.map { (c, ss) ->
        RecipeFormChapterFields(name = c.name, note = c.note, steps = ss.map { (s, ins) ->
          RecipeFormStepFields(
            description = s.description,
            timerMinutes = s.timerMinutes,
            note = s.note,
            ingredients = ins.map { i ->
              RecipeFormIngredientFields(name = i.name, amount = i.amount, unit = i.unit)
            })
        })
      })
  }
}