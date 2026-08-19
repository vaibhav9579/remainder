package com.remainder.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.remainder.app.data.local.entity.ActionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions ORDER BY scheduledDate, scheduledTime")
    fun getAllActions(): Flow<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE isCompleted = 0 ORDER BY scheduledDate, scheduledTime")
    fun getPendingActions(): Flow<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedActions(): Flow<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE scheduledDate = :date ORDER BY scheduledTime")
    fun getActionsForDate(date: LocalDate): Flow<List<ActionEntity>>

    @Query("SELECT * FROM actions WHERE id = :id")
    fun getActionById(id: Long): Flow<ActionEntity?>

    @Query(
        "SELECT * FROM actions WHERE title LIKE '%' || :query || '%' " +
            "OR notes LIKE '%' || :query || '%' " +
            "OR categoryId IN (SELECT id FROM categories WHERE name LIKE '%' || :query || '%') " +
            "ORDER BY scheduledDate, scheduledTime",
    )
    fun searchActions(query: String): Flow<List<ActionEntity>>

    @Insert
    suspend fun insert(action: ActionEntity): Long

    @Update
    suspend fun update(action: ActionEntity)

    @Delete
    suspend fun delete(action: ActionEntity)
}
