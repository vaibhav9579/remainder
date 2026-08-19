package com.remainder.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.usecase.action.CompleteActionUseCase
import com.remainder.app.domain.usecase.action.RestoreActionUseCase
import com.remainder.app.domain.usecase.action.SearchActionsUseCase
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
import javax.inject.Inject

enum class StatusFilter { PENDING, COMPLETED }

fun StatusFilter.displayLabel(): String = when (this) {
    StatusFilter.PENDING -> "Pending"
    StatusFilter.COMPLETED -> "Completed"
}

private data class SearchFilters(
    val status: StatusFilter?,
    val priority: Priority?,
    val categoryId: Long?,
)

data class SearchUiState(
    val query: String = "",
    val statusFilter: StatusFilter? = null,
    val priorityFilter: Priority? = null,
    val categoryFilter: Long? = null,
    val results: List<Action> = emptyList(),
    val categories: List<Category> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    searchActions: SearchActionsUseCase,
    getCategories: GetCategoriesUseCase,
    private val completeAction: CompleteActionUseCase,
    private val restoreAction: RestoreActionUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val statusFilter = MutableStateFlow<StatusFilter?>(null)
    private val priorityFilter = MutableStateFlow<Priority?>(null)
    private val categoryFilter = MutableStateFlow<Long?>(null)

    private val filters = combine(statusFilter, priorityFilter, categoryFilter, ::SearchFilters)
    private val searchResults = query.flatMapLatest { q -> searchActions(q) }

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        filters,
        searchResults,
        getCategories(),
    ) { q, activeFilters, results, categories ->
        SearchUiState(
            query = q,
            statusFilter = activeFilters.status,
            priorityFilter = activeFilters.priority,
            categoryFilter = activeFilters.categoryId,
            results = results.filter { matchesFilters(it, activeFilters) },
            categories = categories,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState())

    private fun matchesFilters(action: Action, filters: SearchFilters): Boolean {
        val statusMatches = when (filters.status) {
            null -> true
            StatusFilter.PENDING -> !action.isCompleted
            StatusFilter.COMPLETED -> action.isCompleted
        }
        val priorityMatches = filters.priority == null || action.priority == filters.priority
        val categoryMatches = filters.categoryId == null || action.categoryId == filters.categoryId
        return statusMatches && priorityMatches && categoryMatches
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onStatusFilterChange(value: StatusFilter?) {
        statusFilter.value = value
    }

    fun onPriorityFilterChange(value: Priority?) {
        priorityFilter.value = value
    }

    fun onCategoryFilterChange(value: Long?) {
        categoryFilter.value = value
    }

    fun onToggleComplete(action: Action) {
        viewModelScope.launch {
            if (action.isCompleted) restoreAction(action) else completeAction(action)
        }
    }
}
