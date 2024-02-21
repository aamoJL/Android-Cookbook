package com.aamo.cookbook.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "recipes")
data class Recipe(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "category") val category: String = "",
  @ColumnInfo(name = "subCategory", defaultValue = "") val subCategory: String = "",
  @ColumnInfo(name = "servings", defaultValue = "1") val servings: Int = 1,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
  @ColumnInfo(name = "thumbnailUri", defaultValue = "") val thumbnailUri: String = "",
)

@Entity(tableName = "recipeChapters",
  foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )]
)
/**
 * @param [orderNumber] Chapter's index number in a recipe. Starts from one.
 * Primarily used to fetch the chapters in the right order from the database.
 * The order number will be assigned when a recipe is saved to the database
 */
data class Chapter(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "orderNumber")
  val orderNumber: Int = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "recipeId") val recipeId: Int = 0,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
)

@Entity(tableName = "chapterSteps",
  foreignKeys = [ForeignKey(
    entity = Chapter::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("chapterId"),
    onDelete = ForeignKey.CASCADE
  )])
/**
 * @param [orderNumber] Step's index number in a chapter. Starts from one.
 * Primarily used to fetch the steps in the right order from the database.
 * The order number will be assigned when a recipe is saved to the database
 */
data class Step(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "orderNumber") val orderNumber: Int = 0,
  @ColumnInfo(name = "description") val description: String = "",
  @ColumnInfo(name = "chapterId") val chapterId: Int = 0,
  @ColumnInfo(name = "timerMinutes", defaultValue = "NULL") val timerMinutes: Int? = null,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
){
  /**
   * Returns the description as a string that ends with ':' or '.',
   * depending if the step has any ingredients.
   */
  fun getDescriptionWithFormattedEndChar(isEmpty: Boolean): String {
    return "${description}${if (isEmpty) "." else ":"}"
  }
}

@Entity(tableName = "ingredients",
  foreignKeys = [ForeignKey(
    entity = Step::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("stepId"),
    onDelete = ForeignKey.CASCADE
  )])
data class Ingredient(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "amount", defaultValue = "0") val amount: Float = 0f,
  @ColumnInfo(name = "unit", defaultValue = "") val unit: String = "",
  @ColumnInfo(name = "stepId") val stepId: Int = 0,
)

data class RecipeWithChaptersStepsAndIngredients(
  @Embedded val value: Recipe,
  @Relation(
    entity = Chapter::class,
    parentColumn = "id",
    entityColumn = "recipeId"
  )
  val chapters: List<ChapterWithStepsAndIngredients> = emptyList()
) {
  fun copyAsNew(): RecipeWithChaptersStepsAndIngredients {
    return this.copy(
      value = value.copy(id = 0, thumbnailUri = ""),
      chapters = chapters.map { c ->
        c.copy(
          value = c.value.copy(id = 0),
          steps = c.steps.map { s ->
            s.copy(
              value = s.value.copy(id = 0),
              ingredients = s.ingredients.map { i ->
                i.copy(id = 0)
              }
            )
          })
      }
    )
  }
}

data class ChapterWithStepsAndIngredients(
  @Embedded val value: Chapter,
  @Relation(
    entity = Step::class,
    parentColumn = "id",
    entityColumn = "chapterId"
  )
  val steps: List<StepWithIngredients> = emptyList()
)

data class StepWithIngredients(
  @Embedded val value: Step,
  @Relation(
    entity = Ingredient::class,
    parentColumn = "id",
    entityColumn = "stepId",
  )
  val ingredients: List<Ingredient> = emptyList()
)

data class RecipeCategoryTuple(
  @ColumnInfo(name = "category") val category: String,
  @ColumnInfo(name = "subCategory") val subCategory: String
)

data class RecipeWithFavoriteAndRating(
  @Embedded val value: Recipe,
  @Relation(
    entity = FavoriteRecipe::class,
    parentColumn = "id",
    entityColumn = "recipeId"
  )
  val favorite: FavoriteRecipe?,
  @Relation(
    entity = RecipeRating::class,
    parentColumn = "id",
    entityColumn = "recipeId"
  )
  val rating: RecipeRating?
)