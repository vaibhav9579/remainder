package com.remainder.app.presentation.completed

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

// Shell only — populated once completed actions exist (Phase 3/4).
@Composable
fun CompletedScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Completed")
        RemainderEmptyState(
            message = "No completed actions yet.",
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontal),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletedScreenPreview() {
    RemainderTheme {
        CompletedScreen()
    }
}
