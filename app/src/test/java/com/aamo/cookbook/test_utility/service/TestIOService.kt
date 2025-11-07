package com.aamo.cookbook.test_utility.service

import android.net.Uri
import com.aamo.cookbook.service.IOServiceBase

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