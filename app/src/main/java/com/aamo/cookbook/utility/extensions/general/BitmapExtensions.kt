package com.aamo.cookbook.utility.extensions.general

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

fun correctBitmapOrientation(bitmap: Bitmap, orientation: Int): Bitmap? {
  val matrix = Matrix()
  when (orientation) {
    ExifInterface.ORIENTATION_NORMAL -> return bitmap
    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
    ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
      matrix.setRotate(180f)
      matrix.postScale(-1f, 1f)
    }

    ExifInterface.ORIENTATION_TRANSPOSE -> {
      matrix.setRotate(90f)
      matrix.postScale(-1f, 1f)
    }

    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
    ExifInterface.ORIENTATION_TRANSVERSE -> {
      matrix.setRotate(-90f)
      matrix.postScale(-1f, 1f)
    }

    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
    else -> return bitmap
  }
  try {
    val bmRotated: Bitmap =
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    bitmap.recycle()
    return bmRotated
  }
  catch (e: OutOfMemoryError) {
    e.printStackTrace()
    return null
  }
}