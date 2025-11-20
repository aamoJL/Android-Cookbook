package com.aamo.cookbook.test_utility.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aamo.cookbook.database.RecipeDatabase
import com.aamo.cookbook.database.dao.RecipeDao
import org.junit.After
import org.junit.Before
import java.io.IOException

abstract class RecipeDatabaseTest {
  protected lateinit var database: RecipeDatabase
  protected lateinit var dao: RecipeDao

  @Before
  fun setupDatabase() {
    database = Room.inMemoryDatabaseBuilder(
      context = ApplicationProvider.getApplicationContext(), klass = RecipeDatabase::class.java
    ).build()
    dao = database.recipeDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDatabase() {
    database.close()
  }
}