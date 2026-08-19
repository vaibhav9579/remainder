package com.remainder.app.di

import com.remainder.app.domain.reminder.AlarmScheduler
import com.remainder.app.reminder.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler
}
