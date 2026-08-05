package com.aamo.cookbook.features.home.use_cases

import android.content.ContentResolver
import android.net.Uri

fun loadFromFile(contentResolver: ContentResolver, uri: Uri): String? {
  return contentResolver.openInputStream(uri)
    ?.use { stream -> stream.bufferedReader().use { it.readText() } }
}