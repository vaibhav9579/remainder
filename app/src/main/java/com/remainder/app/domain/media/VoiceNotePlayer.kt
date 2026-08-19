package com.remainder.app.domain.media

/**
 * Abstraction the domain/presentation layers play back voice notes through.
 * The Android-specific MediaPlayer implementation lives in the top-level
 * `media` package.
 */
interface VoiceNotePlayer {
    fun play(path: String, onCompletion: () -> Unit)
    fun stop()
}
