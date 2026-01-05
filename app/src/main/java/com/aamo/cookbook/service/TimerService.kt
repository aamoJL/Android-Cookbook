package com.aamo.cookbook.service

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import kotlin.time.Duration

interface ITimerService {
  fun open() {}
  fun start(title: String, duration: Duration) {}
}

class TimerService(val context: Context) : ITimerService {
  override fun open() {
    context.startActivity(Intent(AlarmClock.ACTION_SHOW_TIMERS))
  }

  override fun start(title: String, duration: Duration) {
    context.startActivity(
      Intent(AlarmClock.ACTION_SET_TIMER).putExtra(
        AlarmClock.EXTRA_LENGTH, duration.inWholeSeconds.toInt()
      ).putExtra(AlarmClock.EXTRA_MESSAGE, title).putExtra(AlarmClock.EXTRA_SKIP_UI, false)
    )
  }
}