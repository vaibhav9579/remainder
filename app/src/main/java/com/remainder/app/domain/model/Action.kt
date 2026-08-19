package com.remainder.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class Action(
    val id: Long = 0,
    val title: String,
    val notes: String? = null,
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime,
    val reminderOffset: ReminderOffset,
    val repeatType: RepeatType,
    val categoryId: Long?,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val completedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
)
