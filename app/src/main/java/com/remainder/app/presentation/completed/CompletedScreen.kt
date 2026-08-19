package com.remainder.app.presentation.completed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.ui.components.ActionCard
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.Spacing

@Composable
fun CompletedScreen(
    onOpenAction: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompletedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Completed")
        if (uiState.actions.isEmpty()) {
            RemainderEmptyState(
                message = "No completed actions yet.",
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = Spacing.screenHorizontal,
                    vertical = Spacing.lg,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                items(uiState.actions, key = { it.id }) { action ->
                    ActionCard(
                        action = action,
                        categoryName = action.categoryId?.let { uiState.categoriesById[it]?.name },
                        onClick = { onOpenAction(action.id) },
                        onToggleComplete = { viewModel.onRestore(action) },
                    )
                }
            }
        }
    }
}
