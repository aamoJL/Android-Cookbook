package com.aamo.cookbook.test_utility.service

import android.net.Uri
import com.aamo.cookbook.service.IPhotoService

open class TestPhotoService : IPhotoService {
  override fun get(fileName: String): Uri {
    return Uri.EMPTY
  }

  override fun delete(fileName: String): Boolean {
    return true
  }

  override fun getTemp(): Uri {
    return Uri.EMPTY
  }
}