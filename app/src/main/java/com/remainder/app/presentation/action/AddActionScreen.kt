package com.remainder.app.presentation.action

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.presentation.common.displayLabel
import com.remainder.app.ui.components.RemainderOptionPickerDialog
import com.remainder.app.ui.components.RemainderPrimaryButton
import com.remainder.app.ui.components.RemainderSelectableField
import com.remainder.app.ui.components.RemainderTextButton
import com.remainder.app.ui.components.RemainderTextField
import com.remainder.app.ui.components.RemainderTopAppBar
import com.remainder.app.ui.theme.Spacing
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditActionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showReminderPicker by rememberSaveable { mutableStateOf(false) }
    var showRepeatPicker by rememberSaveable { mutableStateOf(false) }
    var showCategoryPicker by rememberSaveable { mutableStateOf(false) }
    var showPriorityPicker by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        RemainderTopAppBar(
            title = if (uiState.isEditMode) "Edit Action" else "Add Action",
            onBack = onBack,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.screenHorizontal),
        ) {
            Spacer(Modifier.height(Spacing.lg))
            RemainderTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = "Title",
                placeholder = "e.g. Call client",
                isError = uiState.titleError != null,
                supportingText = uiState.titleError,
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                label = "Notes (optional)",
                singleLine = false,
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderSelectableField(
                label = "Date",
                value = uiState.scheduledDate.displayLabel(),
                onClick = { showDatePicker = true },
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderSelectableField(
                label = "Time",
                value = uiState.scheduledTime.displayLabel(),
                onClick = { showTimePicker = true },
            )
            if (uiState.dateTimeError != null) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = uiState.dateTimeError.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            RemainderSelectableField(
                label = "Reminder",
                value = uiState.reminderOffset.displayLabel(),
                onClick = { showReminderPicker = true },
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderSelectableField(
                label = "Repeat",
                value = uiState.repeatType.displayLabel(),
                onClick = { showRepeatPicker = true },
            )
            Spacer(Modifier.height(Spacing.lg))
            val categoryLabel = categories.find { it.id == uiState.categoryId }?.name ?: "None"
            RemainderSelectableField(
                label = "Category",
                value = categoryLabel,
                onClick = { showCategoryPicker = true },
            )
            Spacer(Modifier.height(Spacing.lg))
            RemainderSelectableField(
                label = "Priority",
                value = uiState.priority.displayLabel(),
                onClick = { showPriorityPicker = true },
            )
            Spacer(Modifier.height(Spacing.xxl))
            RemainderPrimaryButton(
                text = "Save Action",
                onClick = { viewModel.save(onSaved) },
            )
            Spacer(Modifier.height(Spacing.xl))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.scheduledDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                RemainderTextButton(
                    text = "OK",
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onDateChange(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                        }
                        showDatePicker = false
                    },
                )
            },
            dismissButton = {
                RemainderTextButton(text = "Cancel", onClick = { showDatePicker = false })
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.scheduledTime.hour,
            initialMinute = uiState.scheduledTime.minute,
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                RemainderTextButton(
                    text = "OK",
                    onClick = {
                        viewModel.onTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        showTimePicker = false
                    },
                )
            },
            dismissButton = {
                RemainderTextButton(text = "Cancel", onClick = { showTimePicker = false })
            },
        )
    }

    if (showReminderPicker) {
        RemainderOptionPickerDialog(
            title = "Reminder",
            options = ReminderOffset.entries,
            selected = uiState.reminderOffset,
            optionLabel = { it.displayLabel() },
            onOptionSelected = viewModel::onReminderChange,
            onDismiss = { showReminderPicker = false },
        )
    }

    if (showRepeatPicker) {
        RemainderOptionPickerDialog(
            title = "Repeat",
            options = RepeatType.entries,
            selected = uiState.repeatType,
            optionLabel = { it.displayLabel() },
            onOptionSelected = viewModel::onRepeatChange,
            onDismiss = { showRepeatPicker = false },
        )
    }

    if (showCategoryPicker) {
        val categoryOptions: List<Category?> = listOf(null) + categories
        RemainderOptionPickerDialog(
            title = "Category",
            options = categoryOptions,
            selected = categories.find { it.id == uiState.categoryId },
            optionLabel = { it?.name ?: "None" },
            onOptionSelected = { viewModel.onCategoryChange(it?.id) },
            onDismiss = { showCategoryPicker = false },
        )
    }

    if (showPriorityPicker) {
        RemainderOptionPickerDialog(
            title = "Priority",
            options = Priority.entries,
            selected = uiState.priority,
            optionLabel = { it.displayLabel() },
            onOptionSelected = viewModel::onPriorityChange,
            onDismiss = { showPriorityPicker = false },
        )
    }
}
