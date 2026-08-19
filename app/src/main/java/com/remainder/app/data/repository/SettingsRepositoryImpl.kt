package com.remainder.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.remainder.app.domain.model.AppSettings
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[THEME_MODE_KEY]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            defaultReminderOffset = prefs[DEFAULT_REMINDER_OFFSET_KEY]
                ?.let { runCatching { ReminderOffset.valueOf(it) }.getOrNull() }
                ?: ReminderOffset.AT_TIME,
        )
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { it[THEME_MODE_KEY] = themeMode.name }
    }

    override suspend fun setDefaultReminderOffset(offset: ReminderOffset) {
        dataStore.edit { it[DEFAULT_REMINDER_OFFSET_KEY] = offset.name }
    }

    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        private val DEFAULT_REMINDER_OFFSET_KEY = stringPreferencesKey("default_reminder_offset")
    }
}
