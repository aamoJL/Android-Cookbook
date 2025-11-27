package com.aamo.cookbook.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import kotlin.time.Duration

class TimerService(val context: Context) {
  fun open(onError: ((ActivityNotFoundException) -> Unit)?) {
    try {
      context.startActivity(Intent(AlarmClock.ACTION_SHOW_TIMERS))
    }
    catch (e: ActivityNotFoundException) {
      onError?.invoke(e)
    }
  }

  fun start(title: String, duration: Duration, onError: ((ActivityNotFoundException) -> Unit)?) {
    try {
      context.startActivity(
        Intent(AlarmClock.ACTION_SET_TIMER).putExtra(
            AlarmClock.EXTRA_LENGTH,
            duration.inWholeSeconds
          ).putExtra(AlarmClock.EXTRA_MESSAGE, title).putExtra(AlarmClock.EXTRA_SKIP_UI, false)
      )
    }
    catch (e: ActivityNotFoundException) {
      onError?.invoke(e)
    }
  }
}