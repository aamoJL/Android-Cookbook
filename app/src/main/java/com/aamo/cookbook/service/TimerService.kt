package com.aamo.cookbook.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

data object TimerService {
  fun open(context: Context, onError: ((ActivityNotFoundException) -> Unit)?) {
    try {
      context.startActivity(Intent(AlarmClock.ACTION_SHOW_TIMERS))
    }
    catch (e: ActivityNotFoundException) {
      onError?.invoke(e)
    }
  }
}