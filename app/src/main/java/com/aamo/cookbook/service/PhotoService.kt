package com.aamo.cookbook.service

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.aamo.cookbook.BuildConfig
import java.io.File
import java.util.Objects

interface IPhotoService {
  fun get(fileName: String): Uri
  fun delete(fileName: String): Boolean
  fun getTemp(): Uri
}

class PhotoService(val context: Context) : IPhotoService {
  override fun get(fileName: String): Uri {
    return IOService(context = context).getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
  }

  override fun delete(fileName: String): Boolean {
    return IOService(context).deleteExternalFile(Environment.DIRECTORY_PICTURES, fileName)
  }

  override fun getTemp(): Uri {
    val storageDir: File? = IOService(context).getExternalFileDir(Environment.DIRECTORY_PICTURES)

    return FileProvider.getUriForFile(
      Objects.requireNonNull(context),
      "${BuildConfig.APPLICATION_ID}.provider",
      File.createTempFile(
        System.currentTimeMillis().toString(), /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
      )
    )
  }
}