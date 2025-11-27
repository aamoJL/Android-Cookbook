package com.aamo.cookbook.test_utility.service

import android.net.Uri
import com.aamo.cookbook.service.IIOService
import java.io.File

class TestIOService : IIOService {
  override fun getFileNameWithSuffixFromUri(uri: Uri): String? {
    return null
  }

  override fun deleteExternalFile(subFolder: String, fileName: String): Boolean {
    return false
  }

  override fun getExternalFileUri(subFolder: String?, fileName: String): Uri {
    throw NotImplementedError()
  }

  override fun getExternalFileDir(subFolder: String): File {
    throw NotImplementedError()
  }
}