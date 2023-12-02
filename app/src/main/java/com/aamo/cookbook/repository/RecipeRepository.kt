package com.aamo.cookbook.repository

import com.aamo.cookbook.model.Recipe

class RecipeRepository{

  val recipes = listOf<Recipe>(

  // Pääruoka
//  Recipe("Kalakeitto", "Pääruoka", "Keitto"),
//  Recipe("Jauhelihakeitto", "Pääruoka", "Keitto"),
//  Recipe("Riisipuuro", "Pääruoka", "Puuro"),
//  Recipe("Kaurapuuro", "Pääruoka", "Puuro"),

  // Jälkiruoka
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
      )),
      Recipe.Chapter.Step("Lisää vähitellen", setOf<Recipe.Ingredient>(
        Recipe.Ingredient("Vehnäjauho", 780f, "g"),
        Recipe.Ingredient("Sulatettu voi", 100f, "g"),
      )),
      Recipe.Chapter.Step("Kohota n. 45 minuuttia"),
    )),
    Recipe.Chapter("Täyte", setOf<Recipe.Chapter.Step>(
      Recipe.Chapter.Step("Kauli taikina levyksi"),
      Recipe.Chapter.Step("Levitä pinnalle", setOf<Recipe.Ingredient>(
        Recipe.Ingredient("Huoneenlämpöinen voi", 60f, "g"),
        Recipe.Ingredient("Fariinisokeri", 1f, "dl"),
        Recipe.Ingredient("Kaneli", 2f, "rkl"),
        Recipe.Ingredient("Kardemumma", 0f, ""),
      )),
      Recipe.Chapter.Step("Kääri rullalle ja leikkaa pullat"),
      Recipe.Chapter.Step("Kohota n. 20 minuuttia"),
    )),
    Recipe.Chapter("Viimeistely", setOf<Recipe.Chapter.Step>(
      Recipe.Chapter.Step("Voitele", setOf<Recipe.Ingredient>(
        Recipe.Ingredient("Muna", .5f, "kpl"),
        Recipe.Ingredient("Raesokeri", 0f, ""),
      )),
      Recipe.Chapter.Step("Paista 200 asteessa 15 minuuttia"),
    )),
  )),
//  Recipe("Pannukakku", "Jälkiruoka", ""),
//  Recipe("Lettu", "Jälkiruoka", ""),
//  Recipe("Juustokakku", "Jälkiruoka", "Kakku"),
//  Recipe("Täytekakku", "Jälkiruoka", "Kakku"),
//
//  // Kastike
//  Recipe("Hollandaisekastike", "Kastike", ""),
//  Recipe("Ruskeakastike", "Kastike", ""),
//
//  // Muut
//  Recipe("Kermavaahto", "", ""),
  )

  fun loadRecipes(): List<Recipe>{
    return recipes
  }

  fun loadRecipes(category: String): List<Recipe>{
    return recipes.filter { x -> x.category == category }
  }
}