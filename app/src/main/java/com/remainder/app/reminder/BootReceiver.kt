package com.remainder.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.usecase.action.GetPendingActionsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var getPendingActions: GetPendingActionsUseCase

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getPendingActions().first().forEach { action -> alarmScheduler.schedule(action) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
