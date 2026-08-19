package com.remainder.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.remainder.app.domain.usecase.reminder.RescheduleAllRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A previously-scheduled alarm's trigger instant can point at the wrong
 * wall-clock time once the device's time zone changes (e.g. after travel) or
 * the user manually sets the clock — both reschedule every pending alarm.
 */
@AndroidEntryPoint
class TimeChangeReceiver : BroadcastReceiver() {

    @Inject lateinit var rescheduleAllReminders: RescheduleAllRemindersUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_TIMEZONE_CHANGED && intent.action != Intent.ACTION_TIME_CHANGED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                rescheduleAllReminders()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
