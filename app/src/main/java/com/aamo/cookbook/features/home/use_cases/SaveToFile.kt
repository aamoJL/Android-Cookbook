package com.aamo.cookbook.features.home.use_cases

import android.content.ContentResolver
import android.net.Uri

fun saveToFile(contentResolver: ContentResolver, uri: Uri, data: String) {
  @Suppress("HardCodedStringLiteral") contentResolver.openOutputStream(uri, "wt")
    ?.use { stream -> stream.write(data.toByteArray()) }
}