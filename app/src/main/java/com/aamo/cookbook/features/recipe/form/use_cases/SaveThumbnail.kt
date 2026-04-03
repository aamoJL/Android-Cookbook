package com.aamo.cookbook.features.recipe.form.use_cases

import com.aamo.cookbook.service.IIOService
import com.aamo.cookbook.service.IPhotoService
import java.io.File

fun saveThumbnail(
  tempName: String,
  photoService: IPhotoService,
  ioService: IIOService,
): String? {
  val filePath = photoService.get(fileName = tempName).path ?: return null

  return photoService.save(File(filePath))?.let { uri ->
    ioService.getFileNameWithSuffixFromUri(uri)
  }
}