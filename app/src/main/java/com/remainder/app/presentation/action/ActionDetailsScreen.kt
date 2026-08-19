package com.remainder.app.presentation.action

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.remainder.app.R
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.components.RemainderEmptyState
import com.remainder.app.ui.components.RemainderPrimaryButton
import com.remainder.app.ui.components.RemainderTextButton
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.Spacing

@Composable
fun ActionDetailsScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActionDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(title = "Action Details", onBack = onBack)
        val action = uiState.action
        if (action == null) {
            if (!uiState.isLoading) {
                RemainderEmptyState(
                    message = "This action no longer exists.",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.screenHorizontal),
            ) {
                Spacer(Modifier.height(Spacing.lg))
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textDecoration = if (action.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                )
                Spacer(Modifier.height(Spacing.xl))
                DetailRow(label = "Status", value = if (action.isCompleted) "Completed" else "Pending")
                DetailRow(label = "Date", value = action.scheduledDate.displayLabel())
                DetailRow(label = "Time", value = action.scheduledTime.displayLabel())
                DetailRow(label = "Reminder", value = action.reminderOffset.displayLabel())
                DetailRow(label = "Repeat", value = action.repeatType.displayLabel())
                DetailRow(label = "Category", value = uiState.categoryName ?: "Uncategorized")
                DetailRow(label = "Priority", value = action.priority.displayLabel())
                if (!action.notes.isNullOrBlank()) {
                    DetailRow(label = "Notes", value = action.notes)
                }
                if (action.voiceNoteUri != null) {
                    VoiceNoteDetailRow(
                        isPlaying = uiState.isPlayingVoiceNote,
                        onPlay = { viewModel.playVoiceNote() },
                        onStop = { viewModel.stopVoiceNote() },
                    )
                }
                Spacer(Modifier.height(Spacing.xxl))
                if (action.isCompleted) {
                    RemainderPrimaryButton(text = "Restore", onClick = { viewModel.restore() })
                } else {
                    val completeLabel = if (action.repeatType == RepeatType.NEVER) {
                        "Mark as Complete"
                    } else {
                        "Complete & Reschedule"
                    }
                    RemainderPrimaryButton(text = completeLabel, onClick = { viewModel.complete() })
                }
                Spacer(Modifier.height(Spacing.md))
                RemainderTextButton(text = "Edit", onClick = { onEdit(action.id) })
                Spacer(Modifier.height(Spacing.md))
                RemainderTextButton(text = "Delete", onClick = { showDeleteConfirm = true })
                Spacer(Modifier.height(Spacing.xl))
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this action?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                RemainderTextButton(
                    text = "Delete",
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.delete(onDeleted)
                    },
                )
            },
            dismissButton = {
                RemainderTextButton(text = "Cancel", onClick = { showDeleteConfirm = false })
            },
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(Spacing.lg))
    }
}

@Composable
private fun VoiceNoteDetailRow(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Voice note",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = if (isPlaying) onStop else onPlay) {
                Icon(
                    painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow),
                    contentDescription = if (isPlaying) "Stop playback" else "Play voice note",
                )
            }
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = if (isPlaying) "Playing…" else "Tap to play",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Spacer(Modifier.height(Spacing.lg))
    }
}
