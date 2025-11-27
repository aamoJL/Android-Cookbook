@file:Suppress("HardCodedStringLiteral")

package com.aamo.cookbook.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MigrationTest {
  private val testDB = "migration-test"

  @get:Rule val helper: MigrationTestHelper = MigrationTestHelper(
    instrumentation = InstrumentationRegistry.getInstrumentation(),
    databaseClass = RecipeDatabase::class.java
  )

  @Test
  @Throws(IOException::class)
  fun migrateAll() {
    // Create earliest version of the database.
    helper.createDatabase(testDB, 1).apply {
      close()
    }

    for (version in 1..RecipeDatabase.Properties.VERSION) {
      helper.runMigrationsAndValidate(testDB, version, true)
    }
  }
}