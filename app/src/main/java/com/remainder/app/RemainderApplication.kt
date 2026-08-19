package com.remainder.app

import android.app.Application
import com.remainder.app.reminder.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RemainderApplication : Application() {

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannel()
    }
}
