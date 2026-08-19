package com.remainder.app.domain.repository

import com.remainder.app.domain.model.Action
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ActionRepository {
    fun getAllActions(): Flow<List<Action>>
    fun getPendingActions(): Flow<List<Action>>
    fun getCompletedActions(): Flow<List<Action>>
    fun getActionsForDate(date: LocalDate): Flow<List<Action>>
    fun getActionById(id: Long): Flow<Action?>
    fun searchActions(query: String): Flow<List<Action>>

    suspend fun addAction(action: Action): Long
    suspend fun updateAction(action: Action)
    suspend fun deleteAction(action: Action)
}
