package com.aamo.cookbook.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "favoriteRecipes",
  foreignKeys = [ForeignKey(
    entity = Recipe::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("recipeId"),
    onDelete = ForeignKey.CASCADE
  )])
data class FavoriteRecipe(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "recipeId") val recipeId: Int,
)

data class FullFavoriteRecipe(
  @Embedded val favoriteRecipe: FavoriteRecipe,
  @Relation(
    entity = Recipe::class,
    parentColumn = "recipeId",
    entityColumn = "id"
  )
  val recipe: Recipe,
)