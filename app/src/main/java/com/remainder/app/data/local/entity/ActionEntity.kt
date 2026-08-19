package com.remainder.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "actions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("categoryId"),
        Index("scheduledDate"),
        Index("isCompleted"),
    ],
)
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String?,
    val scheduledDate: LocalDate,
    val scheduledTime: LocalTime,
    val reminderOffset: ReminderOffset,
    val repeatType: RepeatType,
    val categoryId: Long?,
    val priority: Priority,
    val isCompleted: Boolean,
    val completedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val voiceNoteUri: String? = null,
)
