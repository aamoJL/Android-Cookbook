package com.aamo.cookbook.service

import android.net.Uri

class TestIOService : IOServiceBase() {
  override fun getFileNameWithSuffixFromUri(uri: Uri): String? {
    return null
  }

  override fun deleteExternalFile(subFolder: String, fileName: String): Boolean {
    return false
  }

  override fun getExternalFileUri(subFolder: String?, fileName: String): Uri {
    throw NotImplementedError()
  }
}