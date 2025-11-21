package com.aamo.cookbook.service

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class CalculatorService(val context: Context) {
  fun open(onError: ((ActivityNotFoundException) -> Unit)? = null) {
    try {
      context.startActivity(
        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      )
    }
    catch (_: ActivityNotFoundException) {
      try {
        // Samsung phones
        context.startActivity(
          Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(
            ComponentName(
              "com.sec.android.app.popupcalculator",
              "com.sec.android.app.popupcalculator.Calculator"
            )
          ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
      }
      catch (e: ActivityNotFoundException) {
        onError?.invoke(e)
      }
    }
  }
}