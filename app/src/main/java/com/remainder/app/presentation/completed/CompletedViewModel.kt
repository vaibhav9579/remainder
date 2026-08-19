package com.remainder.app.presentation.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.usecase.action.GetCompletedActionsUseCase
import com.remainder.app.domain.usecase.action.RestoreActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompletedUiState(
    val actions: List<Action> = emptyList(),
    val categoriesById: Map<Long, Category> = emptyMap(),
)

@HiltViewModel
class CompletedViewModel @Inject constructor(
    getCompletedActions: GetCompletedActionsUseCase,
    getCategories: GetCategoriesUseCase,
    private val restoreAction: RestoreActionUseCase,
) : ViewModel() {

    val uiState: StateFlow<CompletedUiState> = combine(
        getCompletedActions(),
        getCategories(),
    ) { actions, categories ->
        CompletedUiState(actions = actions, categoriesById = categories.associateBy { it.id })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CompletedUiState())

    fun onRestore(action: Action) {
        viewModelScope.launch { restoreAction(action) }
    }
}
