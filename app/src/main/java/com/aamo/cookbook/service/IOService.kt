package com.aamo.cookbook.service

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.File

abstract class IOServiceBase {
  /**
   * Returns the file name with a suffix from the [uri].
   */
  abstract fun getFileNameWithSuffixFromUri(uri: Uri): String?
  /**
   * Deletes file from the given external file directory.
   * @param subFolder folder type from the [Environment] class, e.g. [Environment.DIRECTORY_PICTURES]
   */
  abstract fun deleteExternalFile(subFolder: String, fileName: String) : Boolean
  /**
   * Returns uri for the file in the given external directory.
   * @param subFolder folder type from the [Environment] class, e.g. [Environment.DIRECTORY_PICTURES]
   */
  abstract fun getExternalFileUri(subFolder: String?, fileName: String): Uri
}

class IOService(private val context: Context) : IOServiceBase() {
  fun getExternalFileDir(subFolder: String?): File? = context.getExternalFilesDir(subFolder)

  override fun getFileNameWithSuffixFromUri(uri: Uri): String? {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.moveToFirst()?.also { result ->
      if(result){
        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).also { i ->
          if(i >= 0) fileName = cursor.getString(i)
        }
      }
    }
    cursor?.close()
    return fileName
  }

  override fun deleteExternalFile(subFolder: String, fileName: String) : Boolean {
    val file = File(getExternalFileDir(subFolder), fileName)
    return file.delete()
  }

  override fun getExternalFileUri(subFolder: String?, fileName: String) : Uri {
    return File(getExternalFileDir(subFolder), fileName).toUri()
  }
}