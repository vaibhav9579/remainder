package com.remainder.app.presentation.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.model.Priority
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.components.ActionCard
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderOptionPickerDialog
import com.remainder.app.ui.components.RemainderTextField
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.Spacing

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenAction: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showStatusPicker by rememberSaveable { mutableStateOf(false) }
    var showPriorityPicker by rememberSaveable { mutableStateOf(false) }
    var showCategoryPicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Search", onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontal),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            RemainderTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = "Search actions...",
            )
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                FilterChip(
                    selected = uiState.statusFilter != null,
                    onClick = { showStatusPicker = true },
                    label = { Text(uiState.statusFilter?.displayLabel() ?: "Status") },
                )
                FilterChip(
                    selected = uiState.priorityFilter != null,
                    onClick = { showPriorityPicker = true },
                    label = { Text(uiState.priorityFilter?.displayLabel() ?: "Priority") },
                )
                val categoryLabel = uiState.categories.find { it.id == uiState.categoryFilter }?.name
                FilterChip(
                    selected = uiState.categoryFilter != null,
                    onClick = { showCategoryPicker = true },
                    label = { Text(categoryLabel ?: "Category") },
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            if (uiState.results.isEmpty()) {
                RemainderEmptyState(message = "No matching actions.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    items(uiState.results, key = { it.id }) { action ->
                        ActionCard(
                            action = action,
                            categoryName = action.categoryId?.let { id ->
                                uiState.categories.find { it.id == id }?.name
                            },
                            onClick = { onOpenAction(action.id) },
                            onToggleComplete = { viewModel.onToggleComplete(action) },
                        )
                    }
                }
            }
        }
    }

    if (showStatusPicker) {
        val statusOptions: List<StatusFilter?> = listOf(null, StatusFilter.PENDING, StatusFilter.COMPLETED)
        RemainderOptionPickerDialog(
            title = "Status",
            options = statusOptions,
            selected = uiState.statusFilter,
            optionLabel = { it?.displayLabel() ?: "All" },
            onOptionSelected = viewModel::onStatusFilterChange,
            onDismiss = { showStatusPicker = false },
        )
    }

    if (showPriorityPicker) {
        val priorityOptions: List<Priority?> = listOf(null) + Priority.entries
        RemainderOptionPickerDialog(
            title = "Priority",
            options = priorityOptions,
            selected = uiState.priorityFilter,
            optionLabel = { it?.displayLabel() ?: "All" },
            onOptionSelected = viewModel::onPriorityFilterChange,
            onDismiss = { showPriorityPicker = false },
        )
    }

    if (showCategoryPicker) {
        val categoryOptions: List<Category?> = listOf(null) + uiState.categories
        RemainderOptionPickerDialog(
            title = "Category",
            options = categoryOptions,
            selected = uiState.categories.find { it.id == uiState.categoryFilter },
            optionLabel = { it?.name ?: "All" },
            onOptionSelected = { viewModel.onCategoryFilterChange(it?.id) },
            onDismiss = { showCategoryPicker = false },
        )
    }
}
