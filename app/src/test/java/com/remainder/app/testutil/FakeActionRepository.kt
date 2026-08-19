package com.remainder.app.testutil

import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class FakeActionRepository(initial: List<Action> = emptyList()) : ActionRepository {

    private val actionsFlow = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    val currentActions: List<Action> get() = actionsFlow.value

    override fun getAllActions(): Flow<List<Action>> = actionsFlow

    override fun getPendingActions(): Flow<List<Action>> =
        actionsFlow.map { list -> list.filter { !it.isCompleted } }

    override fun getCompletedActions(): Flow<List<Action>> =
        actionsFlow.map { list -> list.filter { it.isCompleted } }

    override fun getActionsForDate(date: LocalDate): Flow<List<Action>> =
        actionsFlow.map { list -> list.filter { it.scheduledDate == date } }

    override fun getActionById(id: Long): Flow<Action?> =
        actionsFlow.map { list -> list.find { it.id == id } }

    override fun searchActions(query: String): Flow<List<Action>> =
        actionsFlow.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.notes?.contains(query, ignoreCase = true) == true
            }
        }

    override suspend fun addAction(action: Action): Long {
        val id = nextId++
        actionsFlow.value = actionsFlow.value + action.copy(id = id)
        return id
    }

    override suspend fun updateAction(action: Action) {
        actionsFlow.value = actionsFlow.value.map { if (it.id == action.id) action else it }
    }

    override suspend fun deleteAction(action: Action) {
        actionsFlow.value = actionsFlow.value.filterNot { it.id == action.id }
    }
}
