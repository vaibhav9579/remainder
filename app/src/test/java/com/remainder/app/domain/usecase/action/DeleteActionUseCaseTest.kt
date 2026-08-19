package com.remainder.app.domain.usecase.action

import com.remainder.app.testutil.FakeActionRepository
import com.remainder.app.testutil.FakeAlarmScheduler
import com.remainder.app.testutil.FakeVoiceNoteRecorder
import com.remainder.app.testutil.sampleAction
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteActionUseCaseTest {

    private lateinit var actionRepository: FakeActionRepository
    private lateinit var alarmScheduler: FakeAlarmScheduler
    private lateinit var voiceNoteRecorder: FakeVoiceNoteRecorder
    private lateinit var useCase: DeleteActionUseCase

    @Before
    fun setUp() {
        actionRepository = FakeActionRepository()
        alarmScheduler = FakeAlarmScheduler()
        voiceNoteRecorder = FakeVoiceNoteRecorder()
        useCase = DeleteActionUseCase(actionRepository, alarmScheduler, voiceNoteRecorder)
    }

    @Test
    fun invoke_withVoiceNote_deletesTheAudioFileToo() = runTest {
        val id = actionRepository.addAction(sampleAction())
        val saved = actionRepository.currentActions.first { it.id == id }
            .copy(voiceNoteUri = "/fake/voice_notes/recording.m4a")

        useCase(saved)

        assertTrue(voiceNoteRecorder.deletedPaths.contains("/fake/voice_notes/recording.m4a"))
        assertTrue(actionRepository.currentActions.isEmpty())
    }

    @Test
    fun invoke_withoutVoiceNote_doesNotTouchVoiceNoteStorage() = runTest {
        val id = actionRepository.addAction(sampleAction())
        val saved = actionRepository.currentActions.first { it.id == id }

        useCase(saved)

        assertTrue(voiceNoteRecorder.deletedPaths.isEmpty())
    }
}
