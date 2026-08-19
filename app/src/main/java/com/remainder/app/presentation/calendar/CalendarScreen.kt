package com.remainder.app.presentation.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.RemainderTheme
import com.remainder.app.ui.theme.Spacing

// Shell only — month navigation, date selection, and date-scoped actions
// arrive in Phase 7 once the action data layer exists.
@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Calendar")
        RemainderEmptyState(
            message = "Calendar view is coming soon.",
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontal),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    RemainderTheme {
        CalendarScreen()
    }
}
