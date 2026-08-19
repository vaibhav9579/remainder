package com.remainder.app.presentation.common

import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.domain.model.ThemeMode
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Priority.displayLabel(): String = when (this) {
    Priority.LOW -> "Low"
    Priority.MEDIUM -> "Medium"
    Priority.HIGH -> "High"
}

fun RepeatType.displayLabel(): String = when (this) {
    RepeatType.NEVER -> "Never"
    RepeatType.DAILY -> "Daily"
    RepeatType.WEEKLY -> "Weekly"
    RepeatType.MONTHLY -> "Monthly"
    RepeatType.YEARLY -> "Yearly"
}

fun ReminderOffset.displayLabel(): String = when (this) {
    ReminderOffset.AT_TIME -> "At time of action"
    ReminderOffset.MIN_5 -> "5 minutes before"
    ReminderOffset.MIN_10 -> "10 minutes before"
    ReminderOffset.MIN_15 -> "15 minutes before"
    ReminderOffset.MIN_30 -> "30 minutes before"
    ReminderOffset.HOUR_1 -> "1 hour before"
    ReminderOffset.DAY_1 -> "1 day before"
}

fun ThemeMode.displayLabel(): String = when (this) {
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SYSTEM -> "System default"
}

private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

fun LocalTime.displayLabel(): String = format(timeFormatter)

fun LocalDate.displayLabel(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> format(dateFormatter)
    }
}
