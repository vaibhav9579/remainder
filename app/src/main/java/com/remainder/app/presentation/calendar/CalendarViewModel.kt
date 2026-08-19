package com.remainder.app.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.usecase.action.CompleteActionUseCase
import com.remainder.app.domain.usecase.action.GetActionsForDateUseCase
import com.remainder.app.domain.usecase.action.RestoreActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarUiState(
    val displayedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val actionsForSelectedDate: List<Action> = emptyList(),
    val categoriesById: Map<Long, Category> = emptyMap(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    getActionsForDate: GetActionsForDateUseCase,
    getCategories: GetCategoriesUseCase,
    private val completeAction: CompleteActionUseCase,
    private val restoreAction: RestoreActionUseCase,
) : ViewModel() {

    private val displayedMonth = MutableStateFlow(YearMonth.now())
    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val actionsForSelectedDate = selectedDate.flatMapLatest { date -> getActionsForDate(date) }

    val uiState: StateFlow<CalendarUiState> = combine(
        displayedMonth,
        selectedDate,
        actionsForSelectedDate,
        getCategories(),
    ) { month, selected, actions, categories ->
        CalendarUiState(
            displayedMonth = month,
            selectedDate = selected,
            actionsForSelectedDate = actions.sortedBy { it.scheduledTime },
            categoriesById = categories.associateBy { it.id },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CalendarUiState())

    fun onPreviousMonth() {
        displayedMonth.value = displayedMonth.value.minusMonths(1)
    }

    fun onNextMonth() {
        displayedMonth.value = displayedMonth.value.plusMonths(1)
    }

    fun onSelectDate(date: LocalDate) {
        selectedDate.value = date
        displayedMonth.value = YearMonth.from(date)
    }

    fun onToggleComplete(action: Action) {
        viewModelScope.launch {
            if (action.isCompleted) restoreAction(action) else completeAction(action)
        }
    }
}
