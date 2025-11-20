package com.aamo.cookbook.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import kotlin.time.Duration

data object TimerService {
  fun open(context: Context, onError: ((ActivityNotFoundException) -> Unit)?) {
    try {
      context.startActivity(Intent(AlarmClock.ACTION_SHOW_TIMERS))
    }
    catch (e: ActivityNotFoundException) {
      onError?.invoke(e)
    }
  }

  fun start(context: Context, title: String, duration: Duration, onError: ((ActivityNotFoundException) -> Unit)?) {
    try {
      context.startActivity(Intent(AlarmClock.ACTION_SET_TIMER)
        .putExtra(AlarmClock.EXTRA_LENGTH, duration.inWholeSeconds)
        .putExtra(AlarmClock.EXTRA_MESSAGE, title)
        .putExtra(AlarmClock.EXTRA_SKIP_UI, false))
    }
    catch (e: ActivityNotFoundException) {
      onError?.invoke(e)
    }
  }
}