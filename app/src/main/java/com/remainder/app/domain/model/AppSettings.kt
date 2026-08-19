package com.remainder.app.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultReminderOffset: ReminderOffset = ReminderOffset.AT_TIME,
)
