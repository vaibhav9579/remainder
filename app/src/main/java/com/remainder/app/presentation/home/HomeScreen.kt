package com.remainder.app.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.ui.components.ActionCard
import com.remainder.app.ui.components.RemainderCard
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderSectionHeader
import com.remainder.app.ui.components.RemainderTextField
import com.remainder.app.ui.theme.Spacing
import java.time.LocalTime

@Composable
fun HomeScreen(
    onOpenAction: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenHorizontal),
    ) {
        Spacer(Modifier.height(Spacing.xl))
        HomeHeader()
        Spacer(Modifier.height(Spacing.lg))
        RemainderTextField(
            value = "",
            onValueChange = {},
            placeholder = "Search actions...",
        )
        Spacer(Modifier.height(Spacing.xl))
        TodaySummaryCard(
            totalToday = uiState.todayActions.size,
            completedToday = uiState.completedTodayCount,
        )
        Spacer(Modifier.height(Spacing.xxl))
        RemainderSectionHeader(title = "Today's Actions")
        if (uiState.todayActions.isEmpty()) {
            RemainderEmptyState(message = "No actions for today yet.")
        } else {
            uiState.todayActions.forEach { action ->
                Spacer(Modifier.height(Spacing.sm))
                ActionCard(
                    action = action,
                    categoryName = action.categoryId?.let { uiState.categoriesById[it]?.name },
                    onClick = { onOpenAction(action.id) },
                    onToggleComplete = { viewModel.onToggleComplete(action) },
                )
            }
        }
        Spacer(Modifier.height(Spacing.xl))
        RemainderSectionHeader(title = "Upcoming")
        if (uiState.upcomingActions.isEmpty()) {
            RemainderEmptyState(message = "Nothing coming up.")
        } else {
            uiState.upcomingActions.forEach { action ->
                Spacer(Modifier.height(Spacing.sm))
                ActionCard(
                    action = action,
                    categoryName = action.categoryId?.let { uiState.categoriesById[it]?.name },
                    onClick = { onOpenAction(action.id) },
                    onToggleComplete = { viewModel.onToggleComplete(action) },
                )
            }
        }
        Spacer(Modifier.height(Spacing.huge))
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    val hour = remember { LocalTime.now().hour }
    Text(
        text = greetingForHour(hour),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
    )
}

private fun greetingForHour(hour: Int): String = when (hour) {
    in 0..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    else -> "Good evening"
}

@Composable
private fun TodaySummaryCard(
    totalToday: Int,
    completedToday: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalToday == 0) 0f else completedToday.toFloat() / totalToday
    RemainderCard(modifier = modifier) {
        Text(text = "Today", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "$completedToday of $totalToday completed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.sm))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
        )
    }
}
