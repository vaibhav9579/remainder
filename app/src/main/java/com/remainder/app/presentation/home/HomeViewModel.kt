package com.remainder.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.usecase.action.CompleteActionUseCase
import com.remainder.app.domain.usecase.action.GetActionsForDateUseCase
import com.remainder.app.domain.usecase.action.GetPendingActionsUseCase
import com.remainder.app.domain.usecase.action.RestoreActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

data class HomeUiState(
    val todayActions: List<Action> = emptyList(),
    val completedTodayCount: Int = 0,
    val upcomingActions: List<Action> = emptyList(),
    val categoriesById: Map<Long, Category> = emptyMap(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getActionsForDate: GetActionsForDateUseCase,
    getPendingActions: GetPendingActionsUseCase,
    getCategories: GetCategoriesUseCase,
    private val completeAction: CompleteActionUseCase,
    private val restoreAction: RestoreActionUseCase,
) : ViewModel() {

    // Re-derives "today" at midnight so Home doesn't go stale if the app is
    // left open overnight (Today's Actions/Upcoming would otherwise never
    // move on their own).
    private val currentDate: Flow<LocalDate> = flow {
        while (true) {
            val today = LocalDate.now()
            emit(today)
            val millisUntilMidnight = Duration.between(
                LocalDateTime.now(),
                today.plusDays(1).atStartOfDay(),
            ).toMillis().coerceAtLeast(1_000L)
            delay(millisUntilMidnight)
        }
    }

    val uiState: StateFlow<HomeUiState> = currentDate.flatMapLatest { today ->
        combine(
            getActionsForDate(today),
            getPendingActions(),
            getCategories(),
        ) { todayActions, pendingActions, categories ->
            val upcoming = pendingActions
                .filter { it.scheduledDate.isAfter(today) }
                .sortedBy { it.scheduledDate }
                .take(5)
            HomeUiState(
                todayActions = todayActions.sortedBy { it.scheduledTime },
                completedTodayCount = todayActions.count { it.isCompleted },
                upcomingActions = upcoming,
                categoriesById = categories.associateBy { it.id },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onToggleComplete(action: Action) {
        viewModelScope.launch {
            if (action.isCompleted) restoreAction(action) else completeAction(action)
        }
    }
}
