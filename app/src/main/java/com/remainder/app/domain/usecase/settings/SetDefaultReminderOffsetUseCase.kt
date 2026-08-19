package com.remainder.app.domain.usecase.settings

import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.repository.SettingsRepository
import javax.inject.Inject

class SetDefaultReminderOffsetUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(offset: ReminderOffset) {
        settingsRepository.setDefaultReminderOffset(offset)
    }
}
