package com.remainder.app.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.remainder.app.MainActivity
import com.remainder.app.R
import com.remainder.app.domain.model.Action
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Reminders for your scheduled actions"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    @SuppressLint("MissingPermission")
    fun showReminder(action: Action) {
        if (!hasNotificationPermission()) return

        val contentIntent = PendingIntent.getActivity(
            context,
            action.id.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(MainActivity.EXTRA_OPEN_ACTION_ID, action.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(action.title)
            .setContentText(contentTextFor(action))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .addAction(0, "Complete", actionPendingIntent(action.id, AlarmReceiver.ACTION_COMPLETE))
            .addAction(0, "Snooze 10m", actionPendingIntent(action.id, AlarmReceiver.ACTION_SNOOZE))
            .build()

        notificationManager.notify(action.id.toInt(), notification)
    }

    fun cancel(actionId: Long) {
        notificationManager.cancel(actionId.toInt())
    }

    private fun contentTextFor(action: Action): String =
        action.notes?.takeIf { it.isNotBlank() }
            ?: "Scheduled for ${action.scheduledTime.format(timeFormatter)}"

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun actionPendingIntent(actionId: Long, intentAction: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = intentAction
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
        const val CHANNEL_ID = "reminders"
    }
}
