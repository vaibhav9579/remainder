package com.remainder.app.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Exposes the persisted theme mode so the app root can wrap itself in the right theme. */
@HiltViewModel
class AppThemeViewModel @Inject constructor(
    getSettings: GetSettingsUseCase,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = getSettings()
        .map { it.themeMode }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)
}
