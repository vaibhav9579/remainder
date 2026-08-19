package com.remainder.app.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.components.ActionCard
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderSectionHeader
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.Spacing
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarScreen(
    onOpenAction: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val firstDayOfWeek = remember { WeekFields.of(Locale.getDefault()).firstDayOfWeek }

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Calendar")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontal),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            MonthHeader(
                month = uiState.displayedMonth,
                onPrevious = viewModel::onPreviousMonth,
                onNext = viewModel::onNextMonth,
            )
            Spacer(Modifier.height(Spacing.md))
            WeekdayHeader(firstDayOfWeek = firstDayOfWeek)
            Spacer(Modifier.height(Spacing.xs))
            MonthGrid(
                month = uiState.displayedMonth,
                selectedDate = uiState.selectedDate,
                firstDayOfWeek = firstDayOfWeek,
                onSelectDate = viewModel::onSelectDate,
            )
            Spacer(Modifier.height(Spacing.xxl))
            RemainderSectionHeader(title = uiState.selectedDate.displayLabel())
            if (uiState.actionsForSelectedDate.isEmpty()) {
                RemainderEmptyState(message = "No actions on this date.")
            } else {
                uiState.actionsForSelectedDate.forEach { action ->
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
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(
            text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
            style = MaterialTheme.typography.titleLarge,
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun WeekdayHeader(firstDayOfWeek: DayOfWeek, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        repeat(7) { index ->
            val day = firstDayOfWeek.plus(index.toLong())
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    firstDayOfWeek: DayOfWeek,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Not `remember`-ed: recomputed each recomposition so the "today" ring
    // doesn't go stale if the screen stays composed across midnight.
    val today = LocalDate.now()
    val days = remember(month, firstDayOfWeek) { buildMonthGrid(month, firstDayOfWeek) }

    Column(modifier = modifier.fillMaxWidth()) {
        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    DayCell(
                        date = date,
                        isCurrentMonth = YearMonth.from(date) == month,
                        isSelected = date == selectedDate,
                        isToday = date == today,
                        onClick = { onSelectDate(date) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderModifier = if (isToday && !isSelected) {
        Modifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(borderModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}

private fun buildMonthGrid(month: YearMonth, firstDayOfWeek: DayOfWeek): List<LocalDate> {
    val firstOfMonth = month.atDay(1)
    val daysToSubtract = (firstOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7
    val gridStart = firstOfMonth.minusDays(daysToSubtract.toLong())
    return (0 until 42).map { gridStart.plusDays(it.toLong()) }
}
