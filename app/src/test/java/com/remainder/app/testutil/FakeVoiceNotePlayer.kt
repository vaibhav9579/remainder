package com.remainder.app.testutil

import com.remainder.app.domain.media.VoiceNotePlayer

class FakeVoiceNotePlayer : VoiceNotePlayer {
    var playedPath: String? = null
        private set

    override fun play(path: String, onCompletion: () -> Unit) {
        playedPath = path
    }

    override fun stop() {
        playedPath = null
    }
}
