package com.aamo.cookbook.service

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.core.net.toUri
import java.io.File

interface IIOService {
  /**
   * Returns the file name with a suffix from the [uri].
   * */
  fun getFileNameWithSuffixFromUri(uri: Uri): String?
  /**
   * Deletes file from the given external file directory.
   * @param subFolder folder type from the [Environment] class, e.g. [Environment.DIRECTORY_PICTURES]
   */
  fun deleteExternalFile(subFolder: String, fileName: String): Boolean
  /**
   * Returns uri for the file in the given external directory.
   * @param subFolder folder type from the [Environment] class, e.g. [Environment.DIRECTORY_PICTURES]
   */
  fun getExternalFileUri(subFolder: String?, fileName: String): Uri
  /**
   * Returns uri for the subdirectory in the given external directory.
   * @param subFolder folder type from the [Environment] class, e.g. [Environment.DIRECTORY_PICTURES]
   */
  fun getExternalFileDir(subFolder: String): File?

  fun getExternalCacheDir(): File?
  fun getExternalCacheFileUri(fileName: String): Uri
  fun deleteExternalCacheFile(fileName: String): Boolean
}

class IOService(private val context: Context) : IIOService {
  override fun getFileNameWithSuffixFromUri(uri: Uri): String {
    var result: String? = null
    @Suppress("HardCodedStringLiteral") if (uri.scheme.equals("content")) {
      val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
      try {
        if (cursor != null && cursor.moveToFirst()) {
          val i = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          result = cursor.getString(i)
        }
      }
      finally {
        cursor!!.close()
      }
    }
    if (result == null) {
      result = uri.path
      val cut = result!!.lastIndexOf('/')
      if (cut != -1) {
        result = result.substring(cut + 1)
      }
    }
    return result
  }

  override fun deleteExternalFile(subFolder: String, fileName: String): Boolean {
    val file = File(context.getExternalFilesDir(subFolder), fileName)
    return file.delete()
  }

  override fun getExternalFileUri(subFolder: String?, fileName: String): Uri {
    return File(context.getExternalFilesDir(subFolder), fileName).toUri()
  }

  override fun getExternalFileDir(subFolder: String): File? {
    return context.getExternalFilesDir(subFolder)
  }

  override fun getExternalCacheDir(): File? {
    return context.externalCacheDir
  }

  override fun getExternalCacheFileUri(fileName: String): Uri {
    return File(context.externalCacheDir, fileName).toUri()
  }

  override fun deleteExternalCacheFile(fileName: String): Boolean {
    val file = File(context.externalCacheDir, fileName)
    return file.delete()
  }
}