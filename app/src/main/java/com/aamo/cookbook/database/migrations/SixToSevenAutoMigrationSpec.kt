package com.aamo.cookbook.database.migrations

import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec

@Suppress("HardCodedStringLiteral")
@RenameTable(fromTableName = "recipes", toTableName = "recipe")
@RenameTable(fromTableName = "recipeChapters", toTableName = "recipeChapter")
@RenameTable(fromTableName = "chapterSteps", toTableName = "chapterStep")
@RenameTable(fromTableName = "ingredients", toTableName = "ingredient")
@RenameTable(fromTableName = "favoriteRecipes", toTableName = "recipeBookmark")
@RenameTable(fromTableName = "recipeRatings", toTableName = "recipeRating")
class SixToSevenAutoMigrationSpec : AutoMigrationSpec