package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.service.IPhotoService

fun deleteThumbnail(fileName: String, photoService: IPhotoService): Boolean {
  return if (fileName.isNotEmpty()) {
    photoService.delete(fileName)
  }
  else false
}