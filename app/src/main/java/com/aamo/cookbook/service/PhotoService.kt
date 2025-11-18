package com.aamo.cookbook.service

import android.content.Context
import android.net.Uri
import android.os.Environment

class PhotoService(val context: Context) {
  fun get(fileName: String): Uri {
    return IOService(context = context).getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
  }

  fun delete(fileName: String): Boolean {
    TODO("Photo service delete")
  }
}