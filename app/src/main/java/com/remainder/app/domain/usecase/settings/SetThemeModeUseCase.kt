package com.remainder.app.domain.usecase.settings

import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        settingsRepository.setThemeMode(themeMode)
    }
}
