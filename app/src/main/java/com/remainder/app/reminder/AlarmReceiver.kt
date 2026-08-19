package com.remainder.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.domain.usecase.action.CompleteActionUseCase
import com.remainder.app.domain.usecase.action.GetActionByIdUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var getActionById: GetActionByIdUseCase

    @Inject lateinit var completeAction: CompleteActionUseCase

    @Inject lateinit var alarmScheduler: AlarmScheduler

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val actionId = intent.getLongExtra(EXTRA_ACTION_ID, -1L)
        if (actionId == -1L) return

        when (intent.action) {
            ACTION_SHOW_REMINDER -> handleShowReminder(actionId)
            ACTION_COMPLETE -> handleComplete(actionId)
            ACTION_SNOOZE -> handleSnooze(actionId)
        }
    }

    private fun handleShowReminder(actionId: Long) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val action = getActionById(actionId).first()
                if (action != null && !action.isCompleted) {
                    notificationHelper.showReminder(action)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleComplete(actionId: Long) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val action = getActionById(actionId).first()
                if (action != null) {
                    completeAction(action)
                    alarmScheduler.cancel(action)
                }
                notificationHelper.cancel(actionId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun handleSnooze(actionId: Long) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val action = getActionById(actionId).first()
                if (action != null) {
                    alarmScheduler.scheduleSnooze(action, Instant.now().plus(SNOOZE_DURATION))
                }
                notificationHelper.cancel(actionId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_SHOW_REMINDER = "com.remainder.app.action.SHOW_REMINDER"
        const val ACTION_COMPLETE = "com.remainder.app.action.COMPLETE_ACTION"
        const val ACTION_SNOOZE = "com.remainder.app.action.SNOOZE_ACTION"
        const val EXTRA_ACTION_ID = "extra_action_id"
        val SNOOZE_DURATION: Duration = Duration.ofMinutes(10)
    }
}
