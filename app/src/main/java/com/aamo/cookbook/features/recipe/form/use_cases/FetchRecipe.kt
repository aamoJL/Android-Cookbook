package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.features.recipe.form.models.RecipeFormChapterFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormInfoFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormIngredientFields
import com.aamo.cookbook.features.recipe.form.models.RecipeFormStepFields

suspend fun fetchRecipe(dao: RecipeDao, recipeId: Long): RecipeWithChaptersStepsAndIngredients {
  return if (recipeId == 0L) RecipeWithChaptersStepsAndIngredients(recipe = Recipe())
  else dao.getCompleteRecipe(recipeId) ?: throw Exception("Failed to fetch data")
}

fun RecipeFormInfoFields.Companion.fromDao(model: RecipeWithChaptersStepsAndIngredients): RecipeFormInfoFields {
  return model.let { (r, cs) ->
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