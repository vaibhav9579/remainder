package com.remainder.app.testutil

import com.remainder.app.domain.model.AppSettings
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {

    private val settingsFlow = MutableStateFlow(initial)

    override val settings: Flow<AppSettings> = settingsFlow

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsFlow.value = settingsFlow.value.copy(themeMode = themeMode)
    }

    override suspend fun setDefaultReminderOffset(offset: ReminderOffset) {
        settingsFlow.value = settingsFlow.value.copy(defaultReminderOffset = offset)
    }
}
