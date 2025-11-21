package com.aamo.cookbook.features.recipe.view.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.service.IPhotoService

suspend fun updateThumbnail(
  dao: RecipeDao,
  photoService: IPhotoService,
  recipe: Recipe,
  value: String,
) {
  dao.upsert(recipe.copy(thumbnailUri = value))

  // remove old thumbnail
  if (recipe.thumbnailUri.isNotEmpty()) {
    photoService.delete(recipe.thumbnailUri)
  }
}