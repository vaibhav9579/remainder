package com.remainder.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.domain.usecase.settings.GetSettingsUseCase
import com.remainder.app.domain.usecase.settings.SetDefaultReminderOffsetUseCase
import com.remainder.app.domain.usecase.settings.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultReminderOffset: ReminderOffset = ReminderOffset.AT_TIME,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettings: GetSettingsUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val setDefaultReminderOffset: SetDefaultReminderOffsetUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getSettings()
        .map { SettingsUiState(themeMode = it.themeMode, defaultReminderOffset = it.defaultReminderOffset) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { setThemeMode(mode) }
    }

    fun onDefaultReminderOffsetChange(offset: ReminderOffset) {
        viewModelScope.launch { setDefaultReminderOffset(offset) }
    }
}
