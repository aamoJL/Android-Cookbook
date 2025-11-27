package com.aamo.cookbook.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aamo.cookbook.BuildConfig
import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Chapter
import com.aamo.cookbook.database.entities.Ingredient
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.database.entities.RecipeBookmark
import com.aamo.cookbook.database.entities.RecipeRating
import com.aamo.cookbook.database.entities.Step
import com.aamo.cookbook.database.migrations.SixToSevenAutoMigrationSpec

// Remember to update version, when adding migrations
@Suppress("HardCodedStringLiteral")
@Database(
  entities = [Recipe::class, Chapter::class, Step::class, Ingredient::class, RecipeBookmark::class, RecipeRating::class],
  version = RecipeDatabase.Properties.VERSION,
  autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3), AutoMigration(
    from = 3, to = 4
  ), AutoMigration(from = 4, to = 5), AutoMigration(from = 5, to = 6), AutoMigration(
    from = 6, to = 7, spec = SixToSevenAutoMigrationSpec::class
  ), AutoMigration(from = 7, to = 8)]
)
abstract class RecipeDatabase : RoomDatabase() {
  object Properties {
    const val VERSION = 8
  }

  abstract fun recipeDao(): RecipeDao

  companion object {
    private const val DATABASE_NAME = "recipe_database"

    @Volatile private var Instance: RecipeDatabase? = null

    fun getDatabase(context: Context): RecipeDatabase {
      return Instance ?: synchronized(this) {
        val builder = Room.databaseBuilder(context, RecipeDatabase::class.java, DATABASE_NAME)

        // Allow main thread queries on debug build so unit tests can clear the database tables after execution
        if (BuildConfig.DEBUG) {
          builder.allowMainThreadQueries()
        }

        builder.build()
      }.also { Instance = it }
    }
  }
}