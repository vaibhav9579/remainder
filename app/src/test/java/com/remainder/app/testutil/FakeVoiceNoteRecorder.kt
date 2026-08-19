package com.remainder.app.testutil

import com.remainder.app.domain.media.VoiceNoteRecorder

class FakeVoiceNoteRecorder : VoiceNoteRecorder {
    var nextRecordingPath = "/fake/voice_notes/recording.m4a"
    val deletedPaths = mutableListOf<String>()
    var isRecording = false
        private set

    override fun start(): String {
        isRecording = true
        return nextRecordingPath
    }

    override fun stop(): String? {
        isRecording = false
        return nextRecordingPath
    }

    override fun cancel() {
        isRecording = false
    }

    override fun deleteFile(path: String) {
        deletedPaths.add(path)
    }
}
