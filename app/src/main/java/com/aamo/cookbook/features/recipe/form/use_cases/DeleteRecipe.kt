package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.database.dao.RecipeDao
import com.aamo.cookbook.database.entities.Recipe
import com.aamo.cookbook.service.IPhotoService

suspend fun deleteRecipe(
  dao: RecipeDao,
  photoService: IPhotoService,
  recipe: Recipe,
): Boolean {
  if (dao.delete(recipe) > 0) {
    if (recipe.thumbnailUri.isNotEmpty()) photoService.delete(recipe.thumbnailUri)
    return true
  }
  return false
}