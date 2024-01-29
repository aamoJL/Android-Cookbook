package com.aamo.cookbook.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
  private val TEST_DB = "migration-test"

  @get:Rule
  val helper: MigrationTestHelper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    RecipeDatabase::class.java
  )

  @Test
  @Throws(IOException::class)
  fun migrateAll() {
    // Create earliest version of the database.
    helper.createDatabase(TEST_DB, 1).apply {
      close()
    }

    for (autoMigrationVersion in 1..2) {
      helper.runMigrationsAndValidate(TEST_DB, autoMigrationVersion, true)
    }
  }
}