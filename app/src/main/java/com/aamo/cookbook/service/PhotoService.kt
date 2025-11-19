package com.aamo.cookbook.service

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.aamo.cookbook.BuildConfig
import java.io.File
import java.util.Objects

class PhotoService(val context: Context) {
  fun get(fileName: String): Uri {
    return IOService(context = context).getExternalFileUri(Environment.DIRECTORY_PICTURES, fileName)
  }

  fun delete(fileName: String): Boolean {
    TODO("Photo service delete")
  }

  fun getTempFileUri(): Uri {
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