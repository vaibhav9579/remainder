package com.remainder.app.presentation.action

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.usecase.action.CompleteActionUseCase
import com.remainder.app.domain.usecase.action.DeleteActionUseCase
import com.remainder.app.domain.usecase.action.GetActionByIdUseCase
import com.remainder.app.domain.usecase.action.RestoreActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActionDetailsUiState(
    val isLoading: Boolean = true,
    val action: Action? = null,
    val categoryName: String? = null,
)

@HiltViewModel
class ActionDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getActionById: GetActionByIdUseCase,
    getCategories: GetCategoriesUseCase,
    private val completeAction: CompleteActionUseCase,
    private val restoreAction: RestoreActionUseCase,
    private val deleteAction: DeleteActionUseCase,
) : ViewModel() {

    private val actionId: Long = checkNotNull(savedStateHandle.get<Long>("actionId"))

    val uiState: StateFlow<ActionDetailsUiState> = combine(
        getActionById(actionId),
        getCategories(),
    ) { action, categories ->
        ActionDetailsUiState(
            isLoading = false,
            action = action,
            categoryName = action?.categoryId?.let { id -> categories.find { it.id == id }?.name },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ActionDetailsUiState())

    fun complete() {
        viewModelScope.launch { uiState.value.action?.let { completeAction(it) } }
    }

    fun restore() {
        viewModelScope.launch { uiState.value.action?.let { restoreAction(it) } }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            uiState.value.action?.let { deleteAction(it) }
            onDeleted()
        }
    }
}
