package com.remainder.app.presentation.action

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remainder.app.domain.model.Action
import com.remainder.app.domain.model.Category
import com.remainder.app.domain.model.Priority
import com.remainder.app.domain.model.ReminderOffset
import com.remainder.app.domain.model.RepeatType
import com.remainder.app.domain.usecase.action.AddActionUseCase
import com.remainder.app.domain.usecase.action.GetActionByIdUseCase
import com.remainder.app.domain.usecase.action.UpdateActionUseCase
import com.remainder.app.domain.usecase.category.GetCategoriesUseCase
import com.remainder.app.domain.media.VoiceNotePlayer
import com.remainder.app.domain.media.VoiceNoteRecorder
import com.remainder.app.domain.quickadd.QuickAddParser
import com.remainder.app.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class AddEditActionUiState(
    val title: String = "",
    val notes: String = "",
    val scheduledDate: LocalDate = LocalDate.now(),
    val scheduledTime: LocalTime = LocalTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0),
    val reminderOffset: ReminderOffset = ReminderOffset.AT_TIME,
    val repeatType: RepeatType = RepeatType.NEVER,
    val categoryId: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val voiceNoteUri: String? = null,
    val isRecording: Boolean = false,
    val isPlayingVoiceNote: Boolean = false,
    val existingAction: Action? = null,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val dateTimeError: String? = null,
)

@HiltViewModel
class AddEditActionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getActionById: GetActionByIdUseCase,
    getCategories: GetCategoriesUseCase,
    getSettings: GetSettingsUseCase,
    private val addAction: AddActionUseCase,
    private val updateAction: UpdateActionUseCase,
    private val voiceNoteRecorder: VoiceNoteRecorder,
    private val voiceNotePlayer: VoiceNotePlayer,
) : ViewModel() {

    private val actionId: Long? =
        savedStateHandle.get<Long>("actionId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditActionUiState())
    val uiState: StateFlow<AddEditActionUiState> = _uiState.asStateFlow()

    private var didSave = false

    val categories: StateFlow<List<Category>> = getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        val id = actionId
        if (id != null) {
            viewModelScope.launch {
                getActionById(id).collect { action ->
                    if (action != null) {
                        _uiState.update {
                            it.copy(
                                title = action.title,
                                notes = action.notes.orEmpty(),
                                scheduledDate = action.scheduledDate,
                                scheduledTime = action.scheduledTime,
                                reminderOffset = action.reminderOffset,
                                repeatType = action.repeatType,
                                categoryId = action.categoryId,
                                priority = action.priority,
                                voiceNoteUri = action.voiceNoteUri,
                                existingAction = action,
                                isEditMode = true,
                            )
                        }
                    }
                }
            }
        } else {
            viewModelScope.launch {
                val defaultOffset = getSettings().first().defaultReminderOffset
                _uiState.update { it.copy(reminderOffset = defaultOffset) }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = null) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }
    fun onDateChange(value: LocalDate) = _uiState.update { it.copy(scheduledDate = value, dateTimeError = null) }
    fun onTimeChange(value: LocalTime) = _uiState.update { it.copy(scheduledTime = value, dateTimeError = null) }
    fun onReminderChange(value: ReminderOffset) = _uiState.update { it.copy(reminderOffset = value) }
    fun onRepeatChange(value: RepeatType) = _uiState.update { it.copy(repeatType = value) }
    fun onCategoryChange(value: Long?) = _uiState.update { it.copy(categoryId = value) }
    fun onPriorityChange(value: Priority) = _uiState.update { it.copy(priority = value) }

    /**
     * Pre-fills the form from free text (e.g. "Pay rent on the 1st at 9am"). Never saves on its
     * own — the user still reviews and taps Save, since a silent misparse could otherwise create
     * a wrong reminder.
     */
    fun onQuickAddParsed(rawText: String) {
        val result = QuickAddParser.parse(rawText)
        _uiState.update {
            it.copy(
                title = result.title.ifBlank { rawText.trim() },
                scheduledDate = result.date ?: it.scheduledDate,
                scheduledTime = result.time ?: it.scheduledTime,
                titleError = null,
                dateTimeError = null,
            )
        }
    }

    fun onStartRecording() {
        deleteUnsavedDraftVoiceNote()
        voiceNoteRecorder.start()
        _uiState.update { it.copy(isRecording = true, voiceNoteUri = null) }
    }

    fun onStopRecording() {
        val path = voiceNoteRecorder.stop()
        _uiState.update { it.copy(isRecording = false, voiceNoteUri = path) }
    }

    fun onDeleteVoiceNote() {
        deleteUnsavedDraftVoiceNote()
        _uiState.update { it.copy(voiceNoteUri = null) }
    }

    fun onPlayVoiceNote() {
        val path = _uiState.value.voiceNoteUri ?: return
        _uiState.update { it.copy(isPlayingVoiceNote = true) }
        voiceNotePlayer.play(path) {
            _uiState.update { it.copy(isPlayingVoiceNote = false) }
        }
    }

    fun onStopPlayback() {
        voiceNotePlayer.stop()
        _uiState.update { it.copy(isPlayingVoiceNote = false) }
    }

    /** Deletes the current draft file only if it isn't the action's already-persisted voice note. */
    private fun deleteUnsavedDraftVoiceNote() {
        val current = _uiState.value.voiceNoteUri
        val original = _uiState.value.existingAction?.voiceNoteUri
        if (current != null && current != original) {
            voiceNoteRecorder.deleteFile(current)
        }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        val scheduledDateTime = LocalDateTime.of(state.scheduledDate, state.scheduledTime)
        if (scheduledDateTime.isBefore(LocalDateTime.now())) {
            _uiState.update { it.copy(dateTimeError = "Pick a date and time in the future") }
            return
        }

        viewModelScope.launch {
            val existing = state.existingAction
            if (existing != null) {
                if (existing.voiceNoteUri != null && existing.voiceNoteUri != state.voiceNoteUri) {
                    voiceNoteRecorder.deleteFile(existing.voiceNoteUri)
                }
                updateAction(
                    existing.copy(
                        title = state.title.trim(),
                        notes = state.notes.trim().takeIf { it.isNotBlank() },
                        scheduledDate = state.scheduledDate,
                        scheduledTime = state.scheduledTime,
                        reminderOffset = state.reminderOffset,
                        repeatType = state.repeatType,
                        categoryId = state.categoryId,
                        priority = state.priority,
                        voiceNoteUri = state.voiceNoteUri,
                    ),
                )
            } else {
                addAction(
                    Action(
                        title = state.title.trim(),
                        notes = state.notes.trim().takeIf { it.isNotBlank() },
                        scheduledDate = state.scheduledDate,
                        scheduledTime = state.scheduledTime,
                        reminderOffset = state.reminderOffset,
                        repeatType = state.repeatType,
                        categoryId = state.categoryId,
                        priority = state.priority,
                        voiceNoteUri = state.voiceNoteUri,
                        createdAt = Instant.now(),
                        updatedAt = Instant.now(),
                    ),
                )
            }
            didSave = true
            onSaved()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceNotePlayer.stop()
        if (_uiState.value.isRecording) voiceNoteRecorder.cancel()
        if (!didSave) deleteUnsavedDraftVoiceNote()
    }
}
