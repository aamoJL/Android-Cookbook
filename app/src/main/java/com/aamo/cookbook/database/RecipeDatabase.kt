package com.aamo.cookbook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.Step

@Database(entities = [
  Recipe::class,
  Chapter::class,
  Step::class,
  Ingredient::class
], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {
  abstract fun recipeDao(): RecipeDao

  companion object {
    private const val DATABASE_NAME = "recipe_database"

    @Volatile
    private var Instance: RecipeDatabase? = null

    fun getDatabase(context: Context): RecipeDatabase {
      // TODO: migration
      return Instance ?: synchronized(this) {
        Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)
      }.fallbackToDestructiveMigration()
        .build()
        .also { Instance = it }
    }
  }
}