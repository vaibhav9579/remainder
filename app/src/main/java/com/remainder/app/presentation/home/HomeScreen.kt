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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.remainder.app.ui.components.RemainderCard
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderSectionHeader
import com.remainder.app.ui.components.RemainderTextField
import com.remainder.app.ui.theme.RemainderTheme
import com.remainder.app.ui.theme.Spacing
import java.time.LocalTime

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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
        TodaySummaryCard(totalToday = 0, completedToday = 0)
        Spacer(Modifier.height(Spacing.xxl))
        RemainderSectionHeader(title = "Today's Actions")
        RemainderEmptyState(message = "No actions for today yet.")
        Spacer(Modifier.height(Spacing.xl))
        RemainderSectionHeader(title = "Upcoming")
        RemainderEmptyState(message = "Nothing coming up.")
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    RemainderTheme {
        HomeScreen()
    }
}
