package com.remainder.app.domain.repository

import com.remainder.app.domain.model.AppSettings
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setThemeMode(themeMode: ThemeMode)
    suspend fun setDefaultReminderOffset(offset: ReminderOffset)
}
