package com.remainder.app.presentation.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.ThemeMode
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.components.RemainderCard
import com.remainder.app.ui.components.RemainderOptionPickerDialog
import com.remainder.app.ui.components.RemainderSectionHeader
import com.remainder.app.ui.components.RemainderSelectableField
import com.remainder.app.ui.components.RemainderTextButton
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.RemainderExtendedTheme
import com.remainder.app.ui.theme.Spacing

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showReminderPicker by rememberSaveable { mutableStateOf(false) }

    val notificationsGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Settings")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontal),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            RemainderSectionHeader(title = "Appearance")
            Spacer(Modifier.height(Spacing.sm))
            RemainderCard {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    if (index > 0) Spacer(Modifier.height(Spacing.xs))
                    ThemeModeRow(
                        label = mode.displayLabel(),
                        selected = uiState.themeMode == mode,
                        onClick = { viewModel.onThemeModeChange(mode) },
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xxl))
            RemainderSectionHeader(title = "Reminders")
            Spacer(Modifier.height(Spacing.sm))
            RemainderSelectableField(
                label = "Default reminder",
                value = uiState.defaultReminderOffset.displayLabel(),
                onClick = { showReminderPicker = true },
            )

            Spacer(Modifier.height(Spacing.xxl))
            RemainderSectionHeader(title = "Notifications")
            Spacer(Modifier.height(Spacing.sm))
            RemainderCard {
                Text(text = "Notification permission", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = if (notificationsGranted) "Granted" else "Not granted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notificationsGranted) {
                        RemainderExtendedTheme.colors.success
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
                if (!notificationsGranted) {
                    Spacer(Modifier.height(Spacing.sm))
                    RemainderTextButton(
                        text = "Open Settings",
                        onClick = {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        },
                    )
                }
            }
            Spacer(Modifier.height(Spacing.huge))
        }
    }

    if (showReminderPicker) {
        RemainderOptionPickerDialog(
            title = "Default reminder",
            options = ReminderOffset.entries,
            selected = uiState.defaultReminderOffset,
            optionLabel = { it.displayLabel() },
            onOptionSelected = viewModel::onDefaultReminderOffsetChange,
            onDismiss = { showReminderPicker = false },
        )
    }
}

@Composable
private fun ThemeModeRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(Spacing.sm))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
