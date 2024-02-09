package com.aamo.cookbook.service

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File

abstract class IOServiceBase {
  abstract fun getFileNameFromUri(uri: Uri): String?
  abstract fun deleteExternalFile(fileName: String) : Boolean
}

class IOService(val context: Context) : IOServiceBase() {
  @SuppressLint("Range")
  override fun getFileNameFromUri(uri: Uri): String? {
    val fileName: String?
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.moveToFirst()
    fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    cursor?.close()
    return fileName
  }

  override fun deleteExternalFile(fileName: String) : Boolean {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
    return file.delete()
  }
}