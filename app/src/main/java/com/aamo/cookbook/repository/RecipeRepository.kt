package com.aamo.cookbook.repository

import com.aamo.cookbook.model.Recipe
import java.util.UUID

class RecipeRepository {

  private val recipes = mutableListOf<Recipe>(
  Recipe("Kanelipullat", "Jälkiruoka", "Pulla", 15, setOf<Recipe.Chapter>(
    Recipe.Chapter("Taikina", setOf<Recipe.Chapter.Step>(
      Recipe.Chapter.Step("Sekoita", setOf<Recipe.Ingredient>(
        Recipe.Ingredient("Hiiva", 25f, "g"),
        Recipe.Ingredient("Lämmin neste", 2.5f, "dl"),
      )),
      Recipe.Chapter.Step("Lisää", setOf<Recipe.Ingredient>(
        Recipe.Ingredient("Sokeri", 1f, "dl"),
        Recipe.Ingredient("Kardemumma", 1f, "rkl"),
        Recipe.Ingredient("Vaniljasokeri", .5f, "rkl"),
        Recipe.Ingredient("Suola", .75f, "tl"),
        Recipe.Ingredient("Muna", .5f, "kpl"),
      )
      ),
      Recipe.Chapter.Step(
        "Lisää vähitellen", setOf<Recipe.Ingredient>(
          Recipe.Ingredient("Vehnäjauho", 780f, "g"),
          Recipe.Ingredient("Sulatettu voi", 100f, "g"),
        )
      ),
      Recipe.Chapter.Step("Kohota n. 45 minuuttia"),
    )
    ),
    Recipe.Chapter(
      "Täyte", setOf<Recipe.Chapter.Step>(
        Recipe.Chapter.Step("Kauli taikina levyksi"),
        Recipe.Chapter.Step(
          "Levitä pinnalle", setOf<Recipe.Ingredient>(
            Recipe.Ingredient("Huoneenlämpöinen voi", 60f, "g"),
            Recipe.Ingredient("Fariinisokeri", 1f, "dl"),
            Recipe.Ingredient("Kaneli", 2f, "rkl"),
            Recipe.Ingredient("Kardemumma", 0f, ""),
          )
        ),
        Recipe.Chapter.Step("Kääri rullalle ja leikkaa pullat"),
        Recipe.Chapter.Step("Kohota n. 20 minuuttia"),
      )
    ),
    Recipe.Chapter(
      "Viimeistely", setOf<Recipe.Chapter.Step>(
        Recipe.Chapter.Step(
          "Voitele", setOf<Recipe.Ingredient>(
            Recipe.Ingredient("Muna", .5f, "kpl"),
            Recipe.Ingredient("Raesokeri", 0f, ""),
          )
        ),
        Recipe.Chapter.Step("Paista 200 asteessa 15 minuuttia"),
      )
    ),
  )
  ),
    Recipe(
      "Kääretorttu (piparkakku)", "Jälkiruoka", "Torttu", 15, setOf<Recipe.Chapter>(
        Recipe.Chapter(
          "Taikina", setOf(
            Recipe.Chapter.Step(
              "Sekoita keskenään", setOf(
                Recipe.Ingredient("Ruokaöljyä", 60f, "g"),
                Recipe.Ingredient("Maitoa", 80f, "g"),
                Recipe.Ingredient("Erikoisvehnäjauho", 100f, "g"),
              )
            ),
            Recipe.Chapter.Step(
              "Lisää joukkoon", setOf(
                Recipe.Ingredient("Keltuaista", 6f, ""),
                Recipe.Ingredient("Vaniljasokeria", 0.5f, "tl"),
                Recipe.Ingredient("Piparkakkumaustetta", 1f, "rkl"),
              )
            ),
            Recipe.Chapter.Step(
              "Sekoita keskenään", setOf(
                Recipe.Ingredient("Valkuiaista", 6f, ""),
                Recipe.Ingredient("Sitruunamehua", 2f, "g"),
                Recipe.Ingredient("Sokeria", 65f, "g"),
              )
            ),
            Recipe.Chapter.Step("Sekoita marenki ja taikina keskenään"),
            Recipe.Chapter.Step("Levitä taikina pellille"),
            Recipe.Chapter.Step("Paista 170° 15 minuuttia")
          )
        ),
        Recipe.Chapter(
          "Täyte", setOf(
            Recipe.Chapter.Step(
              "Sekoita keskenään", setOf(
                Recipe.Ingredient("Tuorejuusto / rahka", 100f, "g"),
                Recipe.Ingredient("Sokeria", 1.5f, "rkl"),
                Recipe.Ingredient("Vaniljasokeria", 0.5f, "tl"),
              )
            ),
            Recipe.Chapter.Step(
              "Lisää joukkoon", setOf(
                Recipe.Ingredient("Kermaa", 2f, "dl"),
                Recipe.Ingredient("Piparkakkumaustetta", 1f, "rkl"),
              )
            ),
          )
        ),
        Recipe.Chapter(
          "Viimeistely", setOf(
            Recipe.Chapter.Step("Levitä täyte taikinan päälle"),
            Recipe.Chapter.Step("Rullaa torttu"),
            Recipe.Chapter.Step("Pidä jääkaapissa 1 tunti")
          )
        ),
      )
    ),
  )

  fun getRecipe(id: UUID?) : Recipe? {
    return recipes.firstOrNull { x -> x.id == id }
  }

  fun getRecipes(): List<Recipe>{
    return recipes
  }

  fun getRecipes(category: String): List<Recipe> {
    return recipes.filter { x -> x.category == category }
  }

  fun addRecipe(recipe: Recipe) : Boolean {
    if(getRecipe(recipe.id) == null){
      recipes.add(recipe)
      return true
    }
    return false
  }

  fun addOrUpdate(recipe: Recipe) : Boolean {
    return if(getRecipe(recipe.id) != null){
      updateRecipe(recipe)
    }
    else {
      addRecipe(recipe)
    }
  }

  private fun updateRecipe(recipe: Recipe) : Boolean {
    val index = recipes.indexOfFirst { it.id == recipe.id }

    if(index != -1){
      recipes[index] = recipe
    }

    return index != -1
  }

  fun removeRecipe(id: UUID): Boolean {
    val index = recipes.indexOfFirst { it.id == id }

    if(index != -1) {
      recipes.removeAt(index)
    }

    return index != -1
  }
}