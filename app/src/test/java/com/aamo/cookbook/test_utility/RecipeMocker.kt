package com.aamo.cookbook.test_utility

import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.ChapterWithStepsAndIngredients
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeWithChaptersStepsAndIngredients
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.entities.StepWithIngredients

class RecipeMocker(seed: Int = 0) {
  private var recipe: Recipe = Recipe(
    id = 0,
    name = "$seed",
    category = "$seed",
    subCategory = "$seed",
    servings = seed + 1,
    note = "$seed",
    thumbnailUri = "$seed"
  )
  val chapters = mutableListOf<ChapterMocker>()

  fun modify(recipe: (Recipe) -> Recipe): RecipeMocker {
    this.recipe = recipe(this.recipe)

    chapters.forEach { c -> c.modify { it.copy(recipeId = this.recipe.id) } }

    return this
  }

  fun add(vararg chapter: ChapterMocker): RecipeMocker {
    chapters.addAll(chapter.also {
      it.forEachIndexed { i, c ->
        c.modify { c -> c.copy(recipeId = this.recipe.id, orderNumber = chapters.size + 1 + i) }
      }
    })
    return this
  }

  fun withIds(): RecipeMocker {
    modify { it.copy(id = 1) }

    chapters.forEachIndexed { i, c ->
      c.modify { it.copy(id = i + 1L) }
    }

    chapters.flatMap { it.steps }.also { steps ->
      steps.forEachIndexed { i, s ->
        s.modify { it.copy(id = i + 1L) }
      }

      steps.flatMap { it.ingredients }.forEachIndexed { i, ing ->
        ing.modify { it.copy(id = i + 1L) }
      }
    }

    return this
  }

  fun mock(): RecipeWithChaptersStepsAndIngredients {
    return RecipeWithChaptersStepsAndIngredients(
      recipe = recipe, chapters = chapters.map { it.mock() })
  }

  companion object {
    fun getFullMocker(): RecipeMocker {
      return RecipeMocker().add(
        ChapterMocker().add(
          StepMocker().add(IngredientMocker(), IngredientMocker()),
          StepMocker().add(IngredientMocker()),
        ),
        ChapterMocker().add(
          StepMocker().add(IngredientMocker())
        ),
        ChapterMocker().add(
          StepMocker().add(IngredientMocker(), IngredientMocker())
        ),
      )
    }
  }
}

class ChapterMocker(seed: Int = 0) {
  private var chapter = Chapter(
    id = 0, orderNumber = seed + 1, name = "$seed", recipeId = 0, note = "$seed"
  )
  val steps = mutableListOf<StepMocker>()

  fun modify(recipe: (Chapter) -> Chapter): ChapterMocker {
    this.chapter = recipe(this.chapter)

    steps.forEach { s -> s.modify { it.copy(chapterId = this.chapter.id) } }

    return this
  }

  fun add(vararg step: StepMocker): ChapterMocker {
    steps.addAll(step.also {
      it.forEachIndexed { i, s ->
        s.modify { s -> s.copy(chapterId = this.chapter.id, orderNumber = steps.size + 1 + i) }
      }
    })
    return this
  }

  fun mock(): ChapterWithStepsAndIngredients {
    return ChapterWithStepsAndIngredients(chapter = chapter, steps = steps.map { it.mock() })
  }
}

class StepMocker(seed: Int = 0) {
  private var step = Step(
    id = 0,
    chapterId = 0,
    orderNumber = 1,
    description = "$seed",
    timerMinutes = if (seed == 0) null else seed,
    note = "$seed"
  )
  val ingredients = mutableListOf<IngredientMocker>()

  fun modify(step: (Step) -> Step): StepMocker {
    this.step = step(this.step)

    ingredients.forEach { i -> i.modify { it.copy(stepId = this.step.id) } }

    return this
  }

  fun add(vararg ingredient: IngredientMocker): StepMocker {
    ingredients.addAll(ingredient.also {
      it.forEach { ing -> ing.modify { ing -> ing.copy(stepId = this.step.id) } }
    })
    return this
  }

  fun mock(): StepWithIngredients {
    return StepWithIngredients(step = step, ingredients = ingredients.map { it.mock() })
  }
}

class IngredientMocker(seed: Int = 0) {
  private var ingredient = Ingredient(
    id = 0, stepId = 0, name = "$seed", amount = seed.toDouble(), unit = "$seed"
  )

  fun modify(ingredient: (Ingredient) -> Ingredient): IngredientMocker {
    this.ingredient = ingredient(this.ingredient)
    return this
  }

  fun mock(): Ingredient {
    return ingredient
  }
}