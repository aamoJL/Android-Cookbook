package com.aamo.cookbook.service

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.aamo.cookbook.BuildConfig
import com.aamo.cookbook.utility.extensions.general.onTrue
import java.io.File
import java.util.Objects

interface IPhotoService {
  fun get(fileName: String): Uri
  fun save(file: File, fileName: String = "${System.currentTimeMillis()}.jpg"): Uri?
  fun delete(fileName: String): Boolean
  fun getTemp(name: String = System.currentTimeMillis().toString()): Uri
}

class PhotoService(val context: Context) : IPhotoService {
  companion object {
    const val TEMP_FILE_EXTENSION: String = ".tmp"
  }

  private val debugTag = "PhotoService"

  override fun get(fileName: String): Uri {
    return if (fileName.endsWith(suffix = TEMP_FILE_EXTENSION)) {
      IOService(context = context).getExternalCacheFileUri(fileName)
    }
    else {
      IOService(context = context).getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
    }
  }

  override fun delete(fileName: String): Boolean {
    return if (fileName.endsWith(suffix = TEMP_FILE_EXTENSION)) {
      IOService(context).deleteExternalCacheFile(fileName).onTrue {
        @Suppress("HardCodedStringLiteral") Log.d(debugTag, "Cache file deleted $fileName")
      }
    }
    else {
      IOService(context).deleteExternalFile(Environment.DIRECTORY_PICTURES, fileName).onTrue {
        @Suppress("HardCodedStringLiteral") Log.d(debugTag, "External file deleted $fileName")
      }
    }
  }

  override fun save(file: File, fileName: String): Uri? {
    val externalDirPath =
      IOService(context = context).getExternalFileDir(Environment.DIRECTORY_PICTURES) ?: return null

    return file.copyTo(target = File(externalDirPath, fileName)).toUri().also {
      @Suppress("HardCodedStringLiteral") Log.d(debugTag, "External file saved $fileName")
    }
  }

  override fun getTemp(name: String): Uri {
    val storageDir: File? = IOService(context).getExternalCacheDir()

    return FileProvider.getUriForFile(
      Objects.requireNonNull(context),
      "${BuildConfig.APPLICATION_ID}.provider",
      File.createTempFile(
        name, /* file name */
        TEMP_FILE_EXTENSION, /* suffix */
        storageDir /* directory */
      )
    )
  }
}