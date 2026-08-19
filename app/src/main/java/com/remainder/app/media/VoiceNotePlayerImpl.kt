package com.remainder.app.media

import android.media.MediaPlayer
import com.remainder.app.domain.media.VoiceNotePlayer
import javax.inject.Inject
import javax.inject.Singleton

// Singleton so starting playback from one screen stops any playback left
// running from another, rather than two MediaPlayers overlapping.
@Singleton
class VoiceNotePlayerImpl @Inject constructor() : VoiceNotePlayer {

    private var player: MediaPlayer? = null

    override fun play(path: String, onCompletion: () -> Unit) {
        stop()
        player = MediaPlayer().apply {
            setDataSource(path)
            setOnCompletionListener { onCompletion() }
            prepare()
            start()
        }
    }

    override fun stop() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
    }
}
