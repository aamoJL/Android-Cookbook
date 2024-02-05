package com.aamo.cookbook.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.model.Chapter
import com.aamo.cookbook.model.FavoriteRecipe
import com.aamo.cookbook.model.Ingredient
import com.aamo.cookbook.model.Recipe
import com.aamo.cookbook.model.RecipeRating
import com.aamo.cookbook.model.Step

@Database(
  entities = [
    Recipe::class,
    Chapter::class,
    Step::class,
    Ingredient::class,
    FavoriteRecipe::class,
    RecipeRating::class],
  version = 4,
  autoMigrations = [
    // Remember to update version, when adding migrations
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3),
    AutoMigration(from = 3, to = 4)]
)
abstract class RecipeDatabase : RoomDatabase() {
  abstract fun recipeDao(): RecipeDao

  companion object {
    private const val DATABASE_NAME = "recipe_database"

    @Volatile
    private var Instance: RecipeDatabase? = null

    fun getDatabase(context: Context): RecipeDatabase {
      return Instance ?: synchronized(this) {
        Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)
      }.build()
    }
  }
}