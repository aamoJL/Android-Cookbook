package com.aamo.cookbook.service

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent

interface ICalculatorService {
  fun open() {}
}

class CalculatorService(val context: Context) : ICalculatorService {
  override fun open() {
    try {
      // Try basic calculator intent
      context.startActivity(
        Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR)
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      )
    }
    catch (_: ActivityNotFoundException) {
      // Try Samsung phones
      context.startActivity(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER).setComponent(
          ComponentName(
            "com.sec.android.app.popupcalculator", "com.sec.android.app.popupcalculator.Calculator"
          )
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      )
    }
  }
}