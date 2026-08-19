package com.remainder.app.presentation.settings

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

// Shell only — theme and notification preferences are wired up in Phase 9.
@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Settings")
        RemainderEmptyState(
            message = "Settings are coming soon.",
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.screenHorizontal),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    RemainderTheme {
        SettingsScreen()
    }
}
