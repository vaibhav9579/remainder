package com.remainder.app.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.reminder.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(action: Action) {
        val triggerTime = triggerTimeFor(action) ?: return
        scheduleAt(action.id, triggerTime)
    }

    override fun scheduleSnooze(action: Action, triggerAt: Instant) {
        scheduleAt(action.id, triggerAt)
    }

    override fun cancel(action: Action) {
        alarmManager.cancel(pendingIntentFor(action.id))
    }

    private fun scheduleAt(actionId: Long, triggerTime: Instant) {
        if (triggerTime.isBefore(Instant.now())) return

        val pendingIntent = pendingIntentFor(actionId)
        val triggerAtMillis = triggerTime.toEpochMilli()
        val canScheduleExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

        try {
            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun pendingIntentFor(actionId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_SHOW_REMINDER
            putExtra(AlarmReceiver.EXTRA_ACTION_ID, actionId)
        }
        return PendingIntent.getBroadcast(
            context,
            actionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        fun triggerTimeFor(action: Action): Instant? {
            val scheduled = LocalDateTime.of(action.scheduledDate, action.scheduledTime)
            val trigger = when (action.reminderOffset) {
                ReminderOffset.AT_TIME -> scheduled
                ReminderOffset.MIN_5 -> scheduled.minusMinutes(5)
                ReminderOffset.MIN_10 -> scheduled.minusMinutes(10)
                ReminderOffset.MIN_15 -> scheduled.minusMinutes(15)
                ReminderOffset.MIN_30 -> scheduled.minusMinutes(30)
                ReminderOffset.HOUR_1 -> scheduled.minusHours(1)
                ReminderOffset.DAY_1 -> scheduled.minusDays(1)
            }
            return trigger.atZone(ZoneId.systemDefault()).toInstant()
        }
    }
}
