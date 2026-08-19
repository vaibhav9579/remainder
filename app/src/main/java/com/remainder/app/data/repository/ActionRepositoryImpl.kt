package com.remainder.app.data.repository

import com.remainder.app.data.local.dao.ActionDao
import com.remainder.app.data.local.entity.toDomain
import com.remainder.app.data.local.entity.toEntity
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.repository.ActionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ActionRepositoryImpl @Inject constructor(
    private val actionDao: ActionDao,
) : ActionRepository {

    override fun getAllActions(): Flow<List<Action>> =
        actionDao.getAllActions().map { entities -> entities.map { it.toDomain() } }

    override fun getPendingActions(): Flow<List<Action>> =
        actionDao.getPendingActions().map { entities -> entities.map { it.toDomain() } }

    override fun getCompletedActions(): Flow<List<Action>> =
        actionDao.getCompletedActions().map { entities -> entities.map { it.toDomain() } }

    override fun getActionsForDate(date: LocalDate): Flow<List<Action>> =
        actionDao.getActionsForDate(date).map { entities -> entities.map { it.toDomain() } }

    override fun getActionById(id: Long): Flow<Action?> =
        actionDao.getActionById(id).map { it?.toDomain() }

    override fun searchActions(query: String): Flow<List<Action>> =
        actionDao.searchActions(query).map { entities -> entities.map { it.toDomain() } }

    override suspend fun addAction(action: Action): Long = actionDao.insert(action.toEntity())

    override suspend fun updateAction(action: Action) = actionDao.update(action.toEntity())

    override suspend fun deleteAction(action: Action) = actionDao.delete(action.toEntity())
}
