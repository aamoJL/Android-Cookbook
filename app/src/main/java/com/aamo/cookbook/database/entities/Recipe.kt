@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

// TODO: rename plurals to singular
@Entity(tableName = "recipes")
data class Recipe(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "category") val category: String = "",
  @ColumnInfo(name = "subCategory", defaultValue = "") val subCategory: String = "",
  @ColumnInfo(name = "servings", defaultValue = "1") val servings: Int = 1,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
  @ColumnInfo(name = "thumbnailUri", defaultValue = "") val thumbnailUri: String = "",
)

@Entity(
  tableName = "recipeChapters", foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )]
)
/**
 * @param [orderNumber] Chapter's order number in a recipe. Starts from one.
 * Primarily used to fetch the chapters in the right order from the database.
 * The order number will be assigned when a recipe is saved to the database
 */
data class Chapter(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "orderNumber") val orderNumber: Int = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "recipeId") val recipeId: Long = 0,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
)

@Entity(
  tableName = "chapterSteps", foreignKeys = [ForeignKey(
    entity = Chapter::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("chapterId"),
    onDelete = ForeignKey.CASCADE
  )]
)
/**
 * @param [orderNumber] Step's order number in a chapter. Starts from one.
 * Primarily used to fetch the steps in the right order from the database.
 * The order number will be assigned when a recipe is saved to the database
 */
data class Step(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "chapterId") val chapterId: Long = 0,
  @ColumnInfo(name = "orderNumber") val orderNumber: Int = 0,
  @ColumnInfo(name = "description") val description: String = "",
  @ColumnInfo(name = "timerMinutes", defaultValue = "NULL") val timerMinutes: Int? = null,
  @ColumnInfo(name = "note", defaultValue = "") val note: String = "",
)

@Entity(
  tableName = "ingredients", foreignKeys = [ForeignKey(
    entity = Step::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("stepId"),
    onDelete = ForeignKey.CASCADE
  )]
)
data class Ingredient(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "stepId") val stepId: Long = 0,
  @ColumnInfo(name = "name") val name: String = "",
  @ColumnInfo(name = "amount", defaultValue = "0") val amount: Float = 0f,
  @ColumnInfo(name = "unit", defaultValue = "") val unit: String = "",
)

// TODO: rename to bookmark
@Entity(
  tableName = "favoriteRecipes", foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )]
)
data class RecipeBookmark(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "recipeId") val recipeId: Long,
)

@Entity(
  tableName = "recipeRatings", foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )]
)
data class RecipeRating(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo(name = "recipeId") val recipeId: Long,
  @ColumnInfo(name = "ratingOutOfFive") val ratingOutOfFive: Int,
)

data class RecipeWithChaptersStepsAndIngredients(
  @Embedded val recipe: Recipe, @Relation(
    entity = Chapter::class, parentColumn = "id", entityColumn = "recipeId"
  ) val chapters: List<ChapterWithStepsAndIngredients> = emptyList()
)

data class ChapterWithStepsAndIngredients(
  @Embedded val chapter: Chapter, @Relation(
    entity = Step::class, parentColumn = "id", entityColumn = "chapterId"
  ) val steps: List<StepWithIngredients> = emptyList()
)

data class StepWithIngredients(
  @Embedded val step: Step, @Relation(
    entity = Ingredient::class, parentColumn = "id", entityColumn = "stepId",
  ) val ingredients: List<Ingredient> = emptyList()
)

data class RecipeWithBookmarkAndRating(
  @Embedded val recipe: Recipe, @Relation(
    entity = RecipeBookmark::class, parentColumn = "id", entityColumn = "recipeId"
  ) val bookmark: RecipeBookmark?, @Relation(
    entity = RecipeRating::class, parentColumn = "id", entityColumn = "recipeId"
  ) val rating: RecipeRating?
)