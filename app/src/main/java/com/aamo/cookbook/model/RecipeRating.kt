package com.aamo.cookbook.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "recipeRatings",
  foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )])
data class RecipeRating(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "ratingOutOfFive") val ratingOutOfFive: Int,
  @ColumnInfo(name = "recipeId") val recipeId: Int,
)